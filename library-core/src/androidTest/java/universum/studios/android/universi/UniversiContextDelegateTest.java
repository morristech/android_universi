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
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import universum.studios.android.dialog.manage.DialogController;
import universum.studios.android.test.BaseInstrumentedTest;
import universum.studios.android.test.TestActivity;
import universum.studios.android.test.TestFragment;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Mockito.mock;

/**
 * @author Martin Albedinsky
 */
@RunWith(AndroidJUnit4.class)
public final class UniversiContextDelegateTest extends BaseInstrumentedTest {
    
	@SuppressWarnings("unused")
	private static final String TAG = "UniversiContextDelegateTest";

	@Test
	public void testInstantiation() {
		assertThat(new TestDelegate(mContext).mContext, is(mContext));
	}

    @Test
	public void testCreateActivityDelegate() {
		assertThat(UniversiContextDelegate.create(mock(TestActivity.class)), is(notNullValue()));
    }

    @Test
	public void testCreateFragmentDelegate() {
	    assertThat(UniversiContextDelegate.create(mock(TestFragment.class)), is(notNullValue()));
    }

	@Test
	public void testSetGetDialogController() {
		final UniversiContextDelegate delegate = new TestDelegate(mContext);
		final DialogController controller = new DialogController(mock(TestActivity.class));
		delegate.setDialogController(controller);
		assertThat(delegate.getDialogController(), is(controller));
	}

	@Test
	public void testSetNullDialogController() {
		final UniversiContextDelegate delegate = new TestDelegate(mContext);
		delegate.setDialogController(null);
		assertThat(delegate.getDialogController(), is(notNullValue()));
	}

	@Test
	public void testSetIsViewCreated() {
		final UniversiContextDelegate delegate = new TestDelegate(mContext);
		delegate.setViewCreated(true);
		assertThat(delegate.isViewCreated(), is(true));
		delegate.setViewCreated(false);
		assertThat(delegate.isViewCreated(), is(false));
	}

	@Test
	public void testIsViewCreatedDefault() {
		assertThat(new TestDelegate(mContext).isViewCreated(), is(false));
	}

	@Test
	public void testSetIsPaused() {
		final UniversiContextDelegate delegate = new TestDelegate(mContext);
		delegate.setPaused(true);
		assertThat(delegate.isPaused(), is(true));
		delegate.setPaused(false);
		assertThat(delegate.isPaused(), is(false));
	}

	@Test
	public void testIsPausedDefault() {
		assertThat(new TestDelegate(mContext).isPaused(), is(false));
	}

	@Test
	public void testRegisterRequest() {
		final UniversiContextDelegate delegate = new TestDelegate(mContext);
		delegate.registerRequest(0x00000001);
		assertThat(delegate.isRequestRegistered(0x00000001), is(true));
	}

	@Test
	public void testUnregisterRequest() {
		final UniversiContextDelegate delegate = new TestDelegate(mContext);
		delegate.registerRequest(0x00000001);
		delegate.unregisterRequest(0x00000001);
		assertThat(delegate.isRequestRegistered(0x00000001), is(false));
	}

    private static class TestDelegate extends UniversiContextDelegate {

	    TestDelegate(@NonNull Context context) {
		    super(context);
	    }

	    @NonNull
	    @Override
	    DialogController instantiateDialogController() {
		    return new DialogController(mock(TestActivity.class));
	    }
    }
}
