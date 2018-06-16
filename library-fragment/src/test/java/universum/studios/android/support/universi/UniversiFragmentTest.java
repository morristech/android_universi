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

import android.Manifest;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import org.junit.Test;
import org.robolectric.annotation.Config;

import universum.studios.android.support.dialog.DialogOptions;
import universum.studios.android.support.dialog.manage.DialogController;
import universum.studios.android.support.dialog.manage.DialogFactory;
import universum.studios.android.support.test.local.RobolectricTestCase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
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
    
	@Test public void testREQUEST_BIND_DATA_INNER() {
		// Arrange:
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
		final TestFragment fragment = new TestFragment();
		fragment.setContextDelegate(mockDelegate);
		when(mockDelegate.isViewCreated()).thenReturn(true);
		// Act:
		fragment.REQUEST_BIND_DATA_INNER.run();
		// Assert:
		assertThat(fragment.onBindDataInvoked, is(true));
		verify(mockDelegate).isViewCreated();
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testInstantiation() {
		// Act:
		final UniversiFragment fragment = new TestFragment();
		// Assert:
		assertThat(fragment.getDialogController(), is(notNullValue()));
	}

    @Test public void testDialogController() {
	    // Arrange:
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
	    final DialogController mockController = mock(DialogController.class);
	    when(mockDelegate.getDialogController()).thenReturn(mockController);
	    final TestFragment fragment = new TestFragment();
	    fragment.setContextDelegate(mockDelegate);
	    // Act + Assert:
	    fragment.setDialogController(mockController);
	    verify(mockDelegate).setDialogController(mockController);
	    assertThat(fragment.getDialogController(), is(mockController));
	    verify(mockDelegate).getDialogController();
	    verifyNoMoreInteractions(mockDelegate);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
	@Test public void testDialogFactory() {
	    // Arrange:
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
	    final DialogFactory mockFactory = mock(DialogFactory.class);
	    when(mockDelegate.getDialogFactory()).thenReturn(mockFactory);
	    final TestFragment fragment = new TestFragment();
	    fragment.setContextDelegate(mockDelegate);
	    // Act + Assert:
	    fragment.setDialogFactory(mockFactory);
	    verify(mockDelegate).setDialogFactory(mockFactory);
	    assertThat(fragment.getDialogFactory(), is(mockFactory));
	    verify(mockDelegate).getDialogFactory();
	    verifyNoMoreInteractions(mockDelegate);
    }

    @Test public void testDialogXmlFactory() {
	    // Arrange:
	    final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
	    final TestFragment fragment = new TestFragment();
	    fragment.setContextDelegate(mockDelegate);
	    final int dialogsResource = XML_DIALOGS_SET_RESOURCE_ID;
	    // Act:
	    fragment.setDialogXmlFactory(dialogsResource);
	    // Assert:
	    verify(mockDelegate).setDialogXmlFactory(dialogsResource);
	    verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testOnViewCreated() {
		// Arrange:
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
		final TestFragment fragment = new TestFragment();
		fragment.setContextDelegate(mockDelegate);
		// Act:
		fragment.onViewCreated(new View(application), null);
		// Assert:
		assertThat(fragment.onBindViewsInvoked, is(true));
		verify(mockDelegate).setViewCreated(true);
		verify(mockDelegate).isRequestRegistered(UniversiContextDelegate.REQUEST_BIND_DATA);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testOnViewCreatedWithRequestToBindData() {
		// Arrange:
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
		final TestFragment fragment = new TestFragment();
		fragment.setContextDelegate(mockDelegate);
		when(mockDelegate.isRequestRegistered(UniversiContextDelegate.REQUEST_BIND_DATA)).thenReturn(true);
		// Act:
		fragment.onViewCreated(new View(application), null);
		// Assert:
		assertThat(fragment.onBindViewsInvoked, is(true));
		assertThat(fragment.onBindDataInvoked, is(true));
		verify(mockDelegate).setViewCreated(true);
		verify(mockDelegate).isRequestRegistered(UniversiContextDelegate.REQUEST_BIND_DATA);
		verify(mockDelegate).unregisterRequest(UniversiContextDelegate.REQUEST_BIND_DATA);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testOnResume() {
		// Arrange:
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
		final TestFragment fragment = new TestFragment();
		fragment.setContextDelegate(mockDelegate);
		// Act:
		fragment.onResume();
		// Assert:
		verify(mockDelegate).setPaused(false);
		verifyNoMoreInteractions(mockDelegate);
	}

    @Test public void testRequestBindData() {
	    // Arrange:
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
	    final TestFragment fragment = new TestFragment();
	    fragment.setContextDelegate(mockDelegate);
	    when(mockDelegate.isViewCreated()).thenReturn(false);
	    fragment.requestBindData();
	    // Act + Assert:
	    assertThat(fragment.onBindDataInvoked, is(false));
	    verify(mockDelegate).isViewCreated();
	    verify(mockDelegate).registerRequest(UniversiContextDelegate.REQUEST_BIND_DATA);
	    verifyNoMoreInteractions(mockDelegate);
    }

    @Test public void testRequestBindDataWhenViewIsCreated() {
	    // Arrange:
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
	    final TestFragment fragment = new TestFragment();
	    fragment.setContextDelegate(mockDelegate);
	    when(mockDelegate.isViewCreated()).thenReturn(true);
	    fragment.requestBindData();
	    // Act + Assert:
	    assertThat(fragment.onBindDataInvoked, is(true));
	    verify(mockDelegate).isViewCreated();
	    verifyNoMoreInteractions(mockDelegate);
    }

    @Test public void testIsActiveNetworkConnected() {
	    // Arrange:
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
	    final TestFragment fragment = new TestFragment();
	    fragment.setContextDelegate(mockDelegate);
	    // Act + Assert:
	    assertThat(fragment.isActiveNetworkConnected(), is(false));
	    verify(mockDelegate).isActiveNetworkConnected();
	    verifyNoMoreInteractions(mockDelegate);
    }

    @Test public void testIsNetworkConnected() {
	    // Arrange:
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
	    final TestFragment fragment = new TestFragment();
	    fragment.setContextDelegate(mockDelegate);
	    // Act + (Assert:
	    assertThat(fragment.isNetworkConnected(ConnectivityManager.TYPE_MOBILE), is(false));
	    verify(mockDelegate).isNetworkConnected(ConnectivityManager.TYPE_MOBILE);
	    verifyNoMoreInteractions(mockDelegate);
    }

	@Test public void testCheckSelfPermission() {}

	@Test public void testShouldShowRequestPermissionRationale() {
		// Arrange:
		final UniversiFragment fragment = new TestFragment();
		// Act + Assert:
		assertThat(fragment.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE), is(false));
	}

	@Test public void testSupportRequestPermissions() {}

	@Config(sdk = Build.VERSION_CODES.M)
	@Test public void testOnRequestPermissionsResult() {
		// Arrange:
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
		final TestFragment fragment = new TestFragment();
		fragment.setContextDelegate(mockDelegate);
		// Act:
		fragment.onRequestPermissionsResult(1, new String[0], new int[0]);
		// Assert:
		verifyZeroInteractions(mockDelegate);
	}

	@Test public void testShowDialogWithId() {
		// Arrange:
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
		final TestFragment fragment = new TestFragment();
		fragment.setContextDelegate(mockDelegate);
		// Act:
		fragment.showDialogWithId(1);
		// Assert:
		verify(mockDelegate).showDialogWithId(eq(1), (DialogOptions) isNull());
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testShowDialogWithIdAndOptions() {
		// Arrange:
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
		final TestFragment fragment = new TestFragment();
		fragment.setContextDelegate(mockDelegate);
		final DialogOptions mockOptions = mock(DialogOptions.class);
		// Act:
		fragment.showDialogWithId(1, mockOptions);
		// Assert:
		verify(mockDelegate).showDialogWithId(1, mockOptions);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testDismissDialogWithId() {
		// Arrange:
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
		final TestFragment fragment = new TestFragment();
		fragment.setContextDelegate(mockDelegate);
		// Act:
		fragment.dismissDialogWithId(1);
		// Assert:
		verify(mockDelegate).dismissDialogWithId(1);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testShowXmlDialog() {
		// Arrange:
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
		final TestFragment fragment = new TestFragment();
		fragment.setContextDelegate(mockDelegate);
		final int dialogResource = XML_DIALOG_RESOURCE_ID;
		// Act:
		fragment.showXmlDialog(dialogResource);
		// Assert:
		verify(mockDelegate).showXmlDialog(eq(dialogResource), (DialogOptions) isNull());
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testShowXmlDialogWithOptions() {
		// Arrange:
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
		final TestFragment fragment = new TestFragment();
		fragment.setContextDelegate(mockDelegate);
		final int dialogResource = XML_DIALOG_RESOURCE_ID;
		final DialogOptions mockOptions = mock(DialogOptions.class);
		// Act:
		fragment.showXmlDialog(dialogResource, mockOptions);
		// Assert:
		verify(mockDelegate).showXmlDialog(dialogResource, mockOptions);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testDismissXmlDialog() {
		// Arrange:
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
		final TestFragment fragment = new TestFragment();
		fragment.setContextDelegate(mockDelegate);
		final int dialogResource = XML_DIALOG_RESOURCE_ID;
		// Act:
		fragment.dismissXmlDialog(dialogResource);
		// Assert:
		verify(mockDelegate).dismissXmlDialog(dialogResource);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testOnPause() {
		// Arrange:
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
		final TestFragment fragment = new TestFragment();
		fragment.setContextDelegate(mockDelegate);
		// Act:
		fragment.onPause();
		// Assert:
		verify(mockDelegate).setPaused(true);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testOnDestroyView() {
		// Arrange:
		final UniversiContextDelegate mockDelegate = mock(UniversiContextDelegate.class);
		final TestFragment fragment = new TestFragment();
		fragment.setContextDelegate(mockDelegate);
		// Act:
		fragment.onDestroyView();
		// Assert:
		verify(mockDelegate).setViewCreated(false);
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