/*
 * *************************************************************************************************
 *                                 Copyright 2017 Universum Studios
 * *************************************************************************************************
 *                  Licensed under the Apache License, Version 2.0 (the "License")
 * -------------------------------------------------------------------------------------------------
 * You may not use this file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 * *************************************************************************************************
 */
package universum.studios.android.support.universi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.annotation.VisibleForTesting;

import universum.studios.android.support.dialog.manage.DialogController;

/**
 * An {@link UniversiContextDelegate} implementation that can be used within context of {@link Fragment}.
 *
 * @author Martin Albedinsky
 * @since 1.0
 *
 * @see UniversiActivityDelegate
 */
public class UniversiFragmentDelegate extends UniversiContextDelegate {

	/*
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "UniversiFragmentDelegate";

	/*
	 * Interface ===================================================================================
	 */

	/*
	 * Static members ==============================================================================
	 */

	/*
	 * Members =====================================================================================
	 */

	/**
	 * Fragment instance for which has been this delegate created.
	 */
	private final Fragment fragment;

	/*
	 * Constructors ================================================================================
	 */

	/**
	 * Creates a new instance of UniversiFragmentDelegate for the given fragment <var>context</var>.
	 *
	 * @see UniversiContextDelegate#UniversiContextDelegate(Context)
	 */
	@VisibleForTesting UniversiFragmentDelegate(@NonNull final Fragment context) {
		super(context.getActivity());
		this.fragment = context;
	}

	/*
	 * Methods =====================================================================================
	 */

	/**
	 * Creates a new instance of UniversiFragmentDelegate for the given <var>fragment</var>.
	 *
	 * @param fragment The fragment context in which will be the new delegate used.
	 * @return Ready to be used delegate.
	 */
	@NonNull public static UniversiFragmentDelegate create(@NonNull final Fragment fragment) {
		return new UniversiFragmentDelegate(fragment);
	}

	/**
	 */
	@Override @NonNull final DialogController instantiateDialogController() {
		return new DialogController(fragment);
	}

	/*
	 * Inner classes ===============================================================================
	 */
}