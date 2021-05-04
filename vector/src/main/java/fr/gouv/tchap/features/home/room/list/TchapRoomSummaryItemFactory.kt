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

package fr.gouv.tchap.features.home.room.list

import android.view.View
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import fr.gouv.tchap.core.data.room.RoomAccessState
import im.vector.app.R
import im.vector.app.core.date.DateFormatKind
import im.vector.app.core.date.VectorDateFormatter
import im.vector.app.core.epoxy.VectorEpoxyModel
import im.vector.app.core.resources.StringProvider
import im.vector.app.core.utils.DebouncedClickListener
import im.vector.app.features.home.AvatarRenderer
import im.vector.app.features.home.room.detail.timeline.format.DisplayableEventFormatter
import im.vector.app.features.home.room.list.RoomInvitationItem_
import im.vector.app.features.home.room.list.RoomListListener
import im.vector.app.features.home.room.list.SpaceChildInfoItem_
import im.vector.app.features.home.room.typing.TypingHelper
import org.matrix.android.sdk.api.session.room.members.ChangeMembershipState
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.model.SpaceChildInfo
import org.matrix.android.sdk.api.util.toMatrixItem
import javax.inject.Inject

class TchapRoomSummaryItemFactory @Inject constructor(private val displayableEventFormatter: DisplayableEventFormatter,
                                                      private val dateFormatter: VectorDateFormatter,
                                                      private val stringProvider: StringProvider,
                                                      private val typingHelper: TypingHelper,
                                                      private val avatarRenderer: AvatarRenderer) {

    fun create(roomSummary: RoomSummary,
               roomChangeMembershipStates: Map<String, ChangeMembershipState>,
               selectedRoomIds: Set<String>,
               listener: RoomListListener?): VectorEpoxyModel<*> {
        return when (roomSummary.membership) {
            Membership.INVITE -> {
                val changeMembershipState = roomChangeMembershipStates[roomSummary.roomId] ?: ChangeMembershipState.Unknown
                createInvitationItem(roomSummary, changeMembershipState, listener)
            }
            else              -> createRoomItem(roomSummary, selectedRoomIds, listener?.let { it::onRoomClicked }, listener?.let { it::onRoomLongClicked })
        }
    }

    fun createSuggestion(spaceChildInfo: SpaceChildInfo,
                         suggestedRoomJoiningStates: Map<String, Async<Unit>>,
                         onJoinClick: View.OnClickListener): VectorEpoxyModel<*> {
        return SpaceChildInfoItem_()
                .id("sug_${spaceChildInfo.childRoomId}")
                .matrixItem(spaceChildInfo.toMatrixItem())
                .avatarRenderer(avatarRenderer)
                .topic(spaceChildInfo.topic)
                .buttonLabel(stringProvider.getString(R.string.join))
                .loading(suggestedRoomJoiningStates[spaceChildInfo.childRoomId] is Loading)
                .memberCount(spaceChildInfo.activeMemberCount ?: 0)
                .buttonClickListener(onJoinClick)
    }

    private fun createInvitationItem(roomSummary: RoomSummary,
                                     changeMembershipState: ChangeMembershipState,
                                     listener: RoomListListener?): VectorEpoxyModel<*> {
        val secondLine = if (roomSummary.isDirect) {
            roomSummary.inviterId
        } else {
            roomSummary.inviterId?.let {
                stringProvider.getString(R.string.invited_by, it)
            }
        }

        return RoomInvitationItem_()
                .id(roomSummary.roomId)
                .avatarRenderer(avatarRenderer)
                .matrixItem(roomSummary.toMatrixItem())
                .secondLine(secondLine)
                .changeMembershipState(changeMembershipState)
                .acceptListener { listener?.onAcceptRoomInvitation(roomSummary) }
                .rejectListener { listener?.onRejectRoomInvitation(roomSummary) }
                .listener { listener?.onRoomClicked(roomSummary) }
    }

    fun createRoomItem(
            roomSummary: RoomSummary,
            selectedRoomIds: Set<String>,
            onClick: ((RoomSummary) -> Unit)?,
            onLongClick: ((RoomSummary) -> Boolean)?
    ): VectorEpoxyModel<*> {
        val unreadCount = roomSummary.notificationCount
        val showHighlighted = roomSummary.highlightCount > 0
        val showSelected = selectedRoomIds.contains(roomSummary.roomId)
        var latestFormattedEvent: CharSequence = ""
        var latestEventTime: CharSequence = ""
        val latestEvent = roomSummary.latestPreviewableEvent
        if (latestEvent != null) {
            latestFormattedEvent = displayableEventFormatter.format(latestEvent, roomSummary.isDirect.not())
            latestEventTime = dateFormatter.format(latestEvent.root.originServerTs, DateFormatKind.ROOM_LIST)
        }
        val typingMessage = typingHelper.getTypingMessage(roomSummary.typingUsers)
        return TchapRoomSummaryItem_()
                .id(roomSummary.roomId)
                .avatarRenderer(avatarRenderer)
                // We do not display shield in the room list anymore
                // .encryptionTrustLevel(roomSummary.roomEncryptionTrustLevel)
                .matrixItem(roomSummary.toMatrixItem())
                .isDirect(roomSummary.isDirect)
                .isEncrypted(roomSummary.isEncrypted)
                // FIXME: Update this with the logic of RoomAccessRules
                .roomAccess(RoomAccessState.PRIVATE)
                .lastEventTime(latestEventTime)
                .typingMessage(typingMessage)
                .lastEvent(latestFormattedEvent.toString())
                .lastFormattedEvent(latestFormattedEvent)
                .showHighlighted(showHighlighted)
                .showSelected(showSelected)
                .hasFailedSending(roomSummary.hasFailedSending)
                .unreadNotificationCount(unreadCount)
                .hasUnreadMessage(roomSummary.hasUnreadMessages)
                // FIXME: Check if room has disabled notifications
                .hasDisabledNotifications(false)
                .hasDraft(roomSummary.userDrafts.isNotEmpty())
                .itemLongClickListener { _ ->
                    onLongClick?.invoke(roomSummary) ?: false
                }
                .itemClickListener(
                        DebouncedClickListener({
                            onClick?.invoke(roomSummary)
                        })
                )
    }
}
