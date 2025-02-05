/*
 * Copyright 2020-2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package im.vector.app.features.call.transfer

import im.vector.app.core.platform.VectorViewEvents

sealed class CallTransferViewEvents : VectorViewEvents {
    object Complete : CallTransferViewEvents()
}
