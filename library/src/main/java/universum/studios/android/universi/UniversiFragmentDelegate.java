/*
 * =================================================================================================
 *                             Copyright (C) 2017 Universum Studios
 * =================================================================================================
 *         Licensed under the Apache License, Version 2.0 or later (further "License" only).
 * -------------------------------------------------------------------------------------------------
 * You may use this file only in compliance with the License. More details and copy of this License 
 * you may obtain at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * You can redistribute, modify or publish any part of the code written within this file but as it 
 * is described in the License, the software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES or CONDITIONS OF ANY KIND.
 * 
 * See the License for the specific language governing permissions and limitations under the License.
 * =================================================================================================
 */
package universum.studios.android.universi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.app.Fragment;

import universum.studios.android.dialog.manage.DialogController;

/**
 * An {@link UniversiContextDelegate} implementation that can be used within context of {@link Fragment}.
 *
 * @author Martin Albedinsky
 * @see UniversiActivityDelegate
 */
final class UniversiFragmentDelegate extends UniversiContextDelegate {

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "UniversiFragmentDelegate";

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Members =====================================================================================
	 */

	/**
	 * Fragment instance for which has been this delegate created.
	 */
	final Fragment fragment;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Creates a new instance of UniversiFragmentDelegate for the given fragment <var>context</var>.
	 *
	 * @see UniversiContextDelegate#UniversiContextDelegate(Context)
	 */
	UniversiFragmentDelegate(@NonNull Fragment context) {
		super(context.getActivity());
		this.fragment = context;
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 */
	@NonNull
	@Override
	DialogController instantiateDialogController() {
		return new DialogController(fragment);
	}

	/**
	 * Inner classes ===============================================================================
	 */
}
