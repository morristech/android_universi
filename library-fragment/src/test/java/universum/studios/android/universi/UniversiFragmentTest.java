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
package universum.studios.android.universi;

import android.Manifest;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import org.junit.Test;
import org.robolectric.annotation.Config;

import universum.studios.android.dialog.DialogOptions;
import universum.studios.android.dialog.manage.DialogController;
import universum.studios.android.dialog.manage.DialogFactory;
import universum.studios.android.test.local.RobolectricTestCase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Martin Albedinsky
 */
public final class UniversiFragmentTest extends RobolectricTestCase {

	private static final int XML_DIALOGS_SET_RESOURCE_ID = 1;
	private static final int XML_DIALOG_RESOURCE_ID = 2;
    
	@Test
	public void testREQUEST_BIND_DATA_INNER() {
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
		final TestFragment fragment = new TestFragment();
		fragment.setContextDelegate(mockDelegate);
		when(mockDelegate.isViewCreated()).thenReturn(true);
		fragment.REQUEST_BIND_DATA_INNER.run();
		assertThat(fragment.onBindDataInvoked, is(true));
		verify(mockDelegate, times(1)).isViewCreated();
		verifyNoMoreInteractions(mockDelegate);
	}

    @Test
	public void testSetGetDialogController() {
	    final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
	    final DialogController mockController = mock(DialogController.class);
	    when(mockDelegate.getDialogController()).thenReturn(mockController);
	    final TestFragment fragment = new TestFragment();
	    fragment.setContextDelegate(mockDelegate);
	    fragment.setDialogController(mockController);
	    verify(mockDelegate, times(1)).setDialogController(mockController);
	    assertThat(fragment.getDialogController(), is(mockController));
	    verify(mockDelegate, times(1)).getDialogController();
	    verifyNoMoreInteractions(mockDelegate);
    }

    @Test
    public void testGetDialogControllerDefault() {
	    assertThat(new TestFragment().getDialogController(), is(notNullValue()));
    }

	@Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
	public void testSetGetDialogFactory() {
	    final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
	    final DialogFactory mockFactory = mock(DialogFactory.class);
	    when(mockDelegate.getDialogFactory()).thenReturn(mockFactory);
	    final TestFragment fragment = new TestFragment();
	    fragment.setContextDelegate(mockDelegate);
	    fragment.setDialogFactory(mockFactory);
	    verify(mockDelegate, times(1)).setDialogFactory(mockFactory);
	    assertThat(fragment.getDialogFactory(), is(mockFactory));
	    verify(mockDelegate, times(1)).getDialogFactory();
	    verifyNoMoreInteractions(mockDelegate);
    }

    @Test
	public void testSetDialogXmlFactory() {
	    final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
	    final TestFragment fragment = new TestFragment();
	    fragment.setContextDelegate(mockDelegate);
	    final int dialogsResource = XML_DIALOGS_SET_RESOURCE_ID;
	    fragment.setDialogXmlFactory(dialogsResource);
	    verify(mockDelegate, times(1)).setDialogXmlFactory(dialogsResource);
	    verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testOnViewCreated() {
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
		final TestFragment fragment = new TestFragment();
		fragment.setContextDelegate(mockDelegate);
		fragment.onViewCreated(new View(application), null);
		assertThat(fragment.onBindViewsInvoked, is(true));
		verify(mockDelegate, times(1)).setViewCreated(true);
		verify(mockDelegate, times(1)).isRequestRegistered(UniversiContextDelegate.REQUEST_BIND_DATA);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testOnViewCreatedWithRequestToBindData() {
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
		final TestFragment fragment = new TestFragment();
		fragment.setContextDelegate(mockDelegate);
		when(mockDelegate.isRequestRegistered(UniversiContextDelegate.REQUEST_BIND_DATA)).thenReturn(true);
		fragment.onViewCreated(new View(application), null);
		assertThat(fragment.onBindViewsInvoked, is(true));
		assertThat(fragment.onBindDataInvoked, is(true));
		verify(mockDelegate, times(1)).setViewCreated(true);
		verify(mockDelegate, times(1)).isRequestRegistered(UniversiContextDelegate.REQUEST_BIND_DATA);
		verify(mockDelegate, times(1)).unregisterRequest(UniversiContextDelegate.REQUEST_BIND_DATA);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testOnResume() {
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
		final TestFragment fragment = new TestFragment();
		fragment.setContextDelegate(mockDelegate);
		fragment.onResume();
		verify(mockDelegate, times(1)).setPaused(false);
		verifyNoMoreInteractions(mockDelegate);
	}

    @Test
	public void testRequestBindData() throws Throwable {
	    final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
	    final TestFragment fragment = new TestFragment();
	    fragment.setContextDelegate(mockDelegate);
	    when(mockDelegate.isViewCreated()).thenReturn(false);
	    fragment.requestBindData();
	    assertThat(fragment.onBindDataInvoked, is(false));
	    verify(mockDelegate, times(1)).isViewCreated();
	    verify(mockDelegate, times(1)).registerRequest(UniversiContextDelegate.REQUEST_BIND_DATA);
	    verifyNoMoreInteractions(mockDelegate);
    }

    @Test
	public void testRequestBindDataWhenViewIsCreated() throws Throwable {
	    final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
	    final TestFragment fragment = new TestFragment();
	    fragment.setContextDelegate(mockDelegate);
	    when(mockDelegate.isViewCreated()).thenReturn(true);
	    fragment.requestBindData();
	    assertThat(fragment.onBindDataInvoked, is(true));
	    verify(mockDelegate, times(1)).isViewCreated();
	    verifyNoMoreInteractions(mockDelegate);
    }

    @Test
	public void testIsActiveNetworkConnected() {
	    final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
	    final TestFragment fragment = new TestFragment();
	    fragment.setContextDelegate(mockDelegate);
	    assertThat(fragment.isActiveNetworkConnected(), is(false));
	    verify(mockDelegate, times(1)).isActiveNetworkConnected();
	    verifyNoMoreInteractions(mockDelegate);
    }

    @Test
	public void testIsNetworkConnected() {
	    final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
	    final TestFragment fragment = new TestFragment();
	    fragment.setContextDelegate(mockDelegate);
	    assertThat(fragment.isNetworkConnected(ConnectivityManager.TYPE_MOBILE), is(false));
	    verify(mockDelegate, times(1)).isNetworkConnected(ConnectivityManager.TYPE_MOBILE);
	    verifyNoMoreInteractions(mockDelegate);
    }

	@Test
	public void testCheckSelfPermission() {}

	@Test
	public void testShouldShowRequestPermissionRationale() {
		assertThat(new TestFragment().shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE), is(false));
	}

	@Test
	public void testSupportRequestPermissions() {}

	@Test
	@Config(sdk = Build.VERSION_CODES.M)
	public void testOnRequestPermissionsResult() {
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
		final TestFragment fragment = new TestFragment();
		fragment.setContextDelegate(mockDelegate);
		fragment.onRequestPermissionsResult(1, new String[0], new int[0]);
		verifyZeroInteractions(mockDelegate);
	}

	@Test
	public void testShowDialogWithId() {
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
		final TestFragment fragment = new TestFragment();
		fragment.setContextDelegate(mockDelegate);
		fragment.showDialogWithId(1);
		verify(mockDelegate, times(1)).showDialogWithId(eq(1), (DialogOptions) isNull());
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testShowDialogWithIdAndOptions() {
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
		final TestFragment fragment = new TestFragment();
		fragment.setContextDelegate(mockDelegate);
		final DialogOptions mockOptions = mock(DialogOptions.class);
		fragment.showDialogWithId(1, mockOptions);
		verify(mockDelegate, times(1)).showDialogWithId(1, mockOptions);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testDismissDialogWithId() {
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
		final TestFragment fragment = new TestFragment();
		fragment.setContextDelegate(mockDelegate);
		fragment.dismissDialogWithId(1);
		verify(mockDelegate, times(1)).dismissDialogWithId(1);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testShowXmlDialog() {
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
		final TestFragment fragment = new TestFragment();
		fragment.setContextDelegate(mockDelegate);
		final int dialogResource = XML_DIALOG_RESOURCE_ID;
		fragment.showXmlDialog(dialogResource);
		verify(mockDelegate, times(1)).showXmlDialog(eq(dialogResource), (DialogOptions) isNull());
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testShowXmlDialogWithOptions() {
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
		final TestFragment fragment = new TestFragment();
		fragment.setContextDelegate(mockDelegate);
		final int dialogResource = XML_DIALOG_RESOURCE_ID;
		final DialogOptions mockOptions = mock(DialogOptions.class);
		fragment.showXmlDialog(dialogResource, mockOptions);
		verify(mockDelegate, times(1)).showXmlDialog(dialogResource, mockOptions);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testDismissXmlDialog() {
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
		final TestFragment fragment = new TestFragment();
		fragment.setContextDelegate(mockDelegate);
		final int dialogResource = XML_DIALOG_RESOURCE_ID;
		fragment.dismissXmlDialog(dialogResource);
		verify(mockDelegate, times(1)).dismissXmlDialog(dialogResource);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testOnPause() {
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
		final TestFragment fragment = new TestFragment();
		fragment.setContextDelegate(mockDelegate);
		fragment.onPause();
		verify(mockDelegate, times(1)).setPaused(true);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testOnDestroyView() {
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
		final TestFragment fragment = new TestFragment();
		fragment.setContextDelegate(mockDelegate);
		fragment.onDestroyView();
		verify(mockDelegate, times(1)).setViewCreated(false);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testOnDestroyViewWithoutContextDelegateInitialized() {
		// Arrange:
		final UniversiFragment fragment = new TestFragment();
		// Act:
		fragment.onDestroyView();
	}

	public static final class TestFragment extends UniversiFragment {

		boolean onBindViewsInvoked, onBindDataInvoked;

		@Override protected void onBindViews(@NonNull final View rootView, @Nullable final Bundle savedInstanceState) {
			super.onBindViews(rootView, savedInstanceState);
			this.onBindViewsInvoked = true;
		}

		@Override protected void onBindData() {
			super.onBindData();
			this.onBindDataInvoked = true;
		}
	}
}