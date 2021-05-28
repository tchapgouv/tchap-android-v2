/*
 * Copyright (c) 2021 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.vector.app.features.room

import android.content.Context
import im.vector.app.R
import org.matrix.android.sdk.api.RoomDisplayNameFallbackProvider

class VectorRoomDisplayNameFallbackProvider(
        private val context: Context
) : RoomDisplayNameFallbackProvider {

    override fun getNameForRoomInvite(): String {
        return context.getString(R.string.room_displayname_room_invite)
    }

    override fun getNameForEmptyRoom(isDirect: Boolean, leftMemberNames: List<String>): String {
        return if (isDirect && leftMemberNames.isNotEmpty()) {
            getNameFor1member(leftMemberNames[0])
        } else {
            context.getString(R.string.room_displayname_empty_room)
        }
    }

    override fun getNameFor1member(name: String) = name

    override fun getNameFor2members(name1: String, name2: String): String {
        return context.getString(R.string.room_displayname_two_members, name1, name2)
    }

    override fun getNameFor3members(name1: String, name2: String, name3: String): String {
        return context.getString(R.string.room_displayname_3_members, name1, name2, name3)
    }

    override fun getNameFor4members(name1: String, name2: String, name3: String, name4: String): String {
        return context.getString(R.string.room_displayname_4_members, name1, name2, name3, name4)
    }

    override fun getNameFor4membersAndMore(name1: String, name2: String, name3: String, remainingCount: Int): String {
        return context.resources.getQuantityString(
                R.plurals.room_displayname_four_and_more_members,
                remainingCount,
                name1,
                name2,
                name3,
                remainingCount
        )
    }
}
