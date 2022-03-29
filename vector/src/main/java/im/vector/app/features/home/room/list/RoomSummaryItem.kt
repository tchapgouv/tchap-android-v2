/*
 * Copyright 2019 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.vector.app.features.home.room.list

import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.amulyakhare.textdrawable.TextDrawable
import fr.gouv.tchap.core.utils.TchapRoomType
import fr.gouv.tchap.core.utils.TchapUtils
import im.vector.app.R
import im.vector.app.core.epoxy.ClickListener
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel
import im.vector.app.core.epoxy.onClick
import im.vector.app.core.extensions.setTextOrHide
import im.vector.app.core.ui.views.PresenceStateImageView
import im.vector.app.features.displayname.getBestName
import im.vector.app.features.home.AvatarRenderer
import im.vector.app.features.themes.ThemeUtils
import im.vector.lib.core.utils.epoxy.charsequence.EpoxyCharSequence
import org.matrix.android.sdk.api.crypto.RoomEncryptionTrustLevel
import org.matrix.android.sdk.api.session.presence.model.UserPresence
import org.matrix.android.sdk.api.util.MatrixItem

@EpoxyModelClass(layout = R.layout.item_tchap_room)
abstract class RoomSummaryItem : VectorEpoxyModel<RoomSummaryItem.Holder>() {

    @EpoxyAttribute lateinit var typingMessage: String
    @EpoxyAttribute lateinit var avatarRenderer: AvatarRenderer
    @EpoxyAttribute lateinit var matrixItem: MatrixItem

    @EpoxyAttribute lateinit var lastFormattedEvent: EpoxyCharSequence
    @EpoxyAttribute lateinit var lastEventTime: String
    @EpoxyAttribute var encryptionTrustLevel: RoomEncryptionTrustLevel? = null
    @EpoxyAttribute var userPresence: UserPresence? = null
    @EpoxyAttribute var showPresence: Boolean = false
    @EpoxyAttribute var izPublic: Boolean = false
    @EpoxyAttribute var unreadNotificationCount: Int = 0
    @EpoxyAttribute var hasUnreadMessage: Boolean = false
    @EpoxyAttribute var hasDraft: Boolean = false
    @EpoxyAttribute var showHighlighted: Boolean = false
    @EpoxyAttribute var hasFailedSending: Boolean = false
    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash) var itemLongClickListener: View.OnLongClickListener? = null
    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash) var itemClickListener: ClickListener? = null
    @EpoxyAttribute var showSelected: Boolean = false

    // Tchap items
    @EpoxyAttribute lateinit var roomType: TchapRoomType

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.rootView.onClick(itemClickListener)
        holder.rootView.setOnLongClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            itemLongClickListener?.onLongClick(it) ?: false
        }
        // Tchap: remove domain from the display name
        holder.titleView.text = TchapUtils.getRoomNameFromDisplayName(matrixItem.getBestName(), roomType)
        holder.lastEventTimeView.text = lastEventTime
        holder.lastEventView.text = lastFormattedEvent.charSequence
        holder.unreadCounterBadgeView.render(UnreadCounterBadgeView.State(unreadNotificationCount, showHighlighted))
        holder.unreadIndentIndicator.isVisible = hasUnreadMessage
        holder.draftView.isVisible = hasDraft
        avatarRenderer.render(matrixItem, holder.avatarImageView)
        // Tchap: deleted in Tchap layout
//        holder.roomAvatarDecorationImageView.render(encryptionTrustLevel)
//        holder.roomAvatarPublicDecorationImageView.isVisible = izPublic
        // Tchap: Set invisible instead of gone to keep view size
        holder.roomFailSendingImageView.isInvisible = !hasFailedSending
        renderSelection(holder, showSelected)
        holder.typingView.setTextOrHide(typingMessage)
        holder.lastEventView.isInvisible = holder.typingView.isVisible
        holder.roomAvatarPresenceImageView.render(showPresence, userPresence)

        // Tchap items
        renderTchapRoomType(holder)
    }

    override fun unbind(holder: Holder) {
        holder.rootView.setOnClickListener(null)
        holder.rootView.setOnLongClickListener(null)
        avatarRenderer.clear(holder.avatarImageView)
        super.unbind(holder)
    }

    private fun renderSelection(holder: Holder, isSelected: Boolean) {
        if (isSelected) {
            holder.avatarCheckedImageView.visibility = View.VISIBLE
            val backgroundColor = ThemeUtils.getColor(holder.view.context, R.attr.colorPrimary)
            val backgroundDrawable = TextDrawable.builder().buildRound("", backgroundColor)
            holder.avatarImageView.setImageDrawable(backgroundDrawable)
        } else {
            holder.avatarCheckedImageView.visibility = View.GONE
            avatarRenderer.render(matrixItem, holder.avatarImageView)
        }
    }

    private fun renderTchapRoomType(holder: Holder) {
        with(holder) {
            when (roomType) {
                TchapRoomType.EXTERNAL -> {
                    domainNameView.text = view.context.getString(R.string.tchap_room_extern_room_type)
                    domainNameView.setTextColor(ContextCompat.getColor(view.context, R.color.tchap_room_external))
                }
                TchapRoomType.FORUM    -> {
                    domainNameView.text = view.context.getString(R.string.tchap_room_forum_type)
                    domainNameView.setTextColor(ContextCompat.getColor(view.context, R.color.tchap_room_forum))
                }
                else                   -> {
                    domainNameView.text = ""
                }
            }

            val drawable = if (roomType == TchapRoomType.FORUM) ContextCompat.getDrawable(view.context, R.drawable.ic_tchap_forum) else null
            avatarRoomTypeImageView.setImageDrawable(drawable)
        }
    }

    class Holder : VectorEpoxyHolder() {
        val titleView by bind<TextView>(R.id.roomNameView)
        val unreadCounterBadgeView by bind<UnreadCounterBadgeView>(R.id.roomUnreadCounterBadgeView)
        val unreadIndentIndicator by bind<View>(R.id.roomUnreadIndicator)
        val lastEventView by bind<TextView>(R.id.roomLastEventView)
        val typingView by bind<TextView>(R.id.roomTypingView)
        val draftView by bind<ImageView>(R.id.roomDraftBadge)
        val lastEventTimeView by bind<TextView>(R.id.roomLastEventTimeView)
        val avatarCheckedImageView by bind<ImageView>(R.id.roomAvatarCheckedImageView)
        val avatarImageView by bind<ImageView>(R.id.roomAvatarImageView)

        // Tchap: deleted in Tchap layout
        // val roomAvatarDecorationImageView by bind<ShieldImageView>(R.id.roomAvatarDecorationImageView)
        // val roomAvatarPublicDecorationImageView by bind<ImageView>(R.id.roomAvatarPublicDecorationImageView)
        val roomFailSendingImageView by bind<ImageView>(R.id.roomFailSendingImageView)
        val roomAvatarPresenceImageView by bind<PresenceStateImageView>(R.id.roomAvatarPresenceImageView)
        val rootView by bind<ViewGroup>(R.id.itemRoomLayout)

        // Tchap items
        val domainNameView by bind<TextView>(R.id.tchapRoomDomainNameView)
        val avatarRoomTypeImageView by bind<ImageView>(R.id.tchapRoomAvatarEncryptedImageView)
    }
}
