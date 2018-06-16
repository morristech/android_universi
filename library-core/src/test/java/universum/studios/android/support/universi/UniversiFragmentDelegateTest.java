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

import android.support.v4.app.Fragment;

import org.junit.Test;

import universum.studios.android.dialog.manage.DialogController;
import universum.studios.android.support.test.local.RobolectricTestCase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Mockito.mock;

/**
 * @author Martin Albedinsky
 */
public final class UniversiFragmentDelegateTest extends RobolectricTestCase {

	@Test public void testInstantiateDialogController() {
		// Arrange:
		final Fragment mockFragment = mock(Fragment.class);
		final UniversiFragmentDelegate delegate = new UniversiFragmentDelegate(mockFragment);
		// Act:
		final DialogController controller = delegate.instantiateDialogController();
		// Assert:
		assertThat(controller, is(notNullValue()));
		assertThat(controller, is(not(delegate.instantiateDialogController())));
	}
}