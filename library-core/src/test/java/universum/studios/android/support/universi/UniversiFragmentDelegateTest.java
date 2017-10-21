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
package universum.studios.android.support.universi;

import android.support.v4.app.Fragment;

import org.junit.Test;

import universum.studios.android.support.test.local.RobolectricTestCase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Mockito.mock;

/**
 * @author Martin Albedinsky
 */
public final class UniversiFragmentDelegateTest extends RobolectricTestCase {
    
	@SuppressWarnings("unused")
	private static final String TAG = "UniversiFragmentDelegateTest";

	private Fragment mMockFragment;

	@Override
	public void beforeTest() throws Exception {
		super.beforeTest();
		this.mMockFragment = mock(Fragment.class);
	}

	@Test
	public void testInstantiateDialogController() {
		assertThat(new UniversiFragmentDelegate(mMockFragment).instantiateDialogController(), is(notNullValue()));
	}
}
