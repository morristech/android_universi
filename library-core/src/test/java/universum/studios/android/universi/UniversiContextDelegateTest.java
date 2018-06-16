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

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import org.hamcrest.core.Is;
import org.junit.Test;

import universum.studios.android.dialog.manage.DialogController;
import universum.studios.android.dialog.manage.DialogFactory;
import universum.studios.android.dialog.manage.DialogXmlFactory;
import universum.studios.android.test.local.RobolectricTestCase;
import universum.studios.android.test.local.TestActivity;
import universum.studios.android.test.local.TestFragment;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Martin Albedinsky
 */
public final class UniversiContextDelegateTest extends RobolectricTestCase {

	private static final int XML_DIALOGS_SET_RESOURCE_ID = 1;
	private static final int XML_DIALOG_RESOURCE_ID = 2;

	@Test public void testInstantiation() {
		// Act:
		final UniversiContextDelegate delegate = new TestDelegate(application);
		// Assert:
		assertThat(delegate.context, Is.<Context>is(application));
		assertThat(delegate.isViewCreated(), is(false));
		assertThat(delegate.isPaused(), is(false));
	}

	@Test public void testDialogController() {
		// Arrange:
		final UniversiContextDelegate delegate = new TestDelegate();
		final DialogController mockController = mock(DialogController.class);
		final DialogFactory mockFactory = mock(DialogFactory.class);
		// Act + Assert:
		delegate.setDialogFactory(mockFactory);
		delegate.setDialogController(mockController);
		assertThat(delegate.getDialogController(), is(mockController));
		verify(mockController).setFactory(mockFactory);
		verifyNoMoreInteractions(mockController);
	}

	@Test public void testSetNullDialogController() {
		// Arrange:
		final UniversiContextDelegate delegate = new TestDelegate();
		// Act:
		delegate.setDialogController(null);
		// Assert:
		assertThat(delegate.getDialogController(), is(notNullValue()));
	}

	@Test public void testDialogFactory() {
		// Arrange:
		final UniversiContextDelegate delegate = new TestDelegate();
		final DialogFactory mockFactory = mock(DialogFactory.class);
		// Act + Assert:
		delegate.setDialogFactory(mockFactory);
		assertThat(delegate.getDialogFactory(), is(mockFactory));
	}

	@Test public void testDialogXmlFactory() {
		// Arrange:
		final UniversiContextDelegate delegate = new TestDelegate();
		// Act:
		delegate.setDialogXmlFactory(XML_DIALOGS_SET_RESOURCE_ID);
		// Assert:
		assertThat(delegate.getDialogFactory(), is(notNullValue()));
		assertThat(delegate.getDialogFactory(), instanceOf(DialogXmlFactory.class));
	}

	@Test public void testSetDialogXmlFactoryWithZeroResource() {
		// Arrange:
		final UniversiContextDelegate delegate = new TestDelegate();
		delegate.setDialogFactory(mock(DialogFactory.class));
		// Act:
		delegate.setDialogXmlFactory(0);
		// Assert:
		assertThat(delegate.getDialogFactory(), is(nullValue()));
	}

	@Test public void testShowDialogWithId() {
		// Arrange:
		final TestActivity mockActivity = mock(TestActivity.class);
		final FragmentManager mockFragmentManager = mock(FragmentManager.class);
		final DialogFragment mockDialogFragment = mock(DialogFragment.class);
		when(mockActivity.getFragmentManager()).thenReturn(mockFragmentManager);
		final UniversiContextDelegate delegate = new TestDelegate(mockActivity);
		final DialogFactory mockFactory = mock(DialogFactory.class);
		when(mockFactory.isDialogProvided(1)).thenReturn(true);
		when(mockFactory.createDialogTag(1)).thenReturn("Dialog.TAG.1");
		when(mockFactory.createDialog(1, null)).thenReturn(mockDialogFragment);
		delegate.setDialogFactory(mockFactory);
		// Act + Assert:
		assertThat(delegate.showDialogWithId(1, null), is(true));
		verify(mockFactory).isDialogProvided(1);
		verify(mockFactory).createDialogTag(1);
		verify(mockFactory).createDialog(1, null);
		verifyNoMoreInteractions(mockFactory);
	}

	@Test public void testShowDialogWithIdWhenPaused() {
		// Arrange:
		final UniversiContextDelegate delegate = new TestDelegate();
		final DialogFactory mockFactory = mock(DialogFactory.class);
		delegate.setDialogFactory(mockFactory);
		delegate.setPaused(true);
		// Act + Assert:
		assertThat(delegate.showDialogWithId(1, null), is(false));
		verifyZeroInteractions(mockFactory);
	}

	@Test public void testShowDialogWithIdWithoutDialogFactoryAttached() {
		// Arrange:
		final UniversiContextDelegate delegate = new TestDelegate(application);
		// Act + Assert:
		assertThat(delegate.showDialogWithId(1, null), is(false));
	}

	@Test public void testDismissDialogWithId() {
		// Arrange:
		final TestActivity mockActivity = mock(TestActivity.class);
		final FragmentManager mockFragmentManager = mock(FragmentManager.class);
		final DialogFragment mockDialogFragment = mock(DialogFragment.class);
		when(mockFragmentManager.findFragmentByTag("Dialog.TAG.1")).thenReturn(mockDialogFragment);
		when(mockActivity.getFragmentManager()).thenReturn(mockFragmentManager);
		final UniversiContextDelegate delegate = new TestDelegate(mockActivity);
		final DialogFactory mockFactory = mock(DialogFactory.class);
		when(mockFactory.isDialogProvided(1)).thenReturn(true);
		when(mockFactory.createDialogTag(1)).thenReturn("Dialog.TAG.1");
		delegate.setDialogFactory(mockFactory);
		// Act + Assert:
		assertThat(delegate.dismissDialogWithId(1), is(true));
		verify(mockFactory, times(2)).isDialogProvided(1);
		verify(mockFactory, times(2)).createDialogTag(1);
		verifyNoMoreInteractions(mockFactory);
	}

	@Test public void testDismissDialogWithIdWhichIsNotShowing() {
		// Arrange:
		final TestActivity mockActivity = mock(TestActivity.class);
		final FragmentManager mockFragmentManager = mock(FragmentManager.class);
		when(mockFragmentManager.findFragmentByTag("Dialog.TAG.1")).thenReturn(null);
		when(mockActivity.getFragmentManager()).thenReturn(mockFragmentManager);
		final UniversiContextDelegate delegate = new TestDelegate(mockActivity);
		final DialogFactory mockFactory = mock(DialogFactory.class);
		when(mockFactory.isDialogProvided(1)).thenReturn(true);
		when(mockFactory.createDialogTag(1)).thenReturn("Dialog.TAG.1");
		delegate.setDialogFactory(mockFactory);
		// Act + Assert:
		assertThat(delegate.dismissDialogWithId(1), is(false));
		verify(mockFactory, times(2)).isDialogProvided(1);
		verify(mockFactory).createDialogTag(1);
		verifyNoMoreInteractions(mockFactory);
	}

	@Test public void testDismissDialogWithIdWhenPaused() {
		// Arrange:
		final UniversiContextDelegate delegate = new TestDelegate();
		final DialogFactory mockFactory = mock(DialogFactory.class);
		delegate.setDialogFactory(mockFactory);
		delegate.setPaused(true);
		// Act + Assert:
		assertThat(delegate.dismissDialogWithId(1), is(false));
		verifyZeroInteractions(mockFactory);
	}

	@Test public void testDismissDialogWithIdWithoutDialogFactoryAttached() {
		// Arrange:
		final UniversiContextDelegate delegate = new TestDelegate(application);
		// Act + Assert:
		assertThat(delegate.dismissDialogWithId(1), is(false));
	}

	@Test public void testShowXmlDialog() {
		// Ignored, because we would need to import whole dialogs library in order to successfully
		// perform this test.
	}

	@Test public void testShowXmlDialogWhenPaused() {
		// Arrange:
		final UniversiContextDelegate delegate = new TestDelegate();
		delegate.setPaused(true);
		// Act + Assert:
		assertThat(delegate.showXmlDialog(XML_DIALOG_RESOURCE_ID, null), is(false));
	}

	@Test public void testDismissXmlDialog() {
		// Arrange:
		final int dialogResource = XML_DIALOG_RESOURCE_ID;
		final TestActivity mockActivity = mock(TestActivity.class);
		final FragmentManager mockFragmentManager = mock(FragmentManager.class);
		final DialogFragment mockDialogFragment = mock(DialogFragment.class);
		when(mockFragmentManager.findFragmentByTag(DialogXmlFactory.class.getName() + ".TAG." + dialogResource)).thenReturn(mockDialogFragment);
		when(mockActivity.getFragmentManager()).thenReturn(mockFragmentManager);
		final UniversiContextDelegate delegate = new TestDelegate(mockActivity);
		// Act + Assert:
		assertThat(delegate.dismissXmlDialog(dialogResource), is(true));
	}

	@Test public void testDismissXmlDialogWhichIsNotShowing() {
		// Arrange:
		final int dialogResource = XML_DIALOG_RESOURCE_ID;
		final TestActivity mockActivity = mock(TestActivity.class);
		final FragmentManager mockFragmentManager = mock(FragmentManager.class);
		when(mockFragmentManager.findFragmentByTag(DialogXmlFactory.class.getName() + ".TAG." + dialogResource)).thenReturn(null);
		when(mockActivity.getFragmentManager()).thenReturn(mockFragmentManager);
		final UniversiContextDelegate delegate = new TestDelegate(mockActivity);
		// Act + Assert:
		assertThat(delegate.dismissXmlDialog(dialogResource), is(false));
	}

	@Test public void testDismissXmlDialogWhichIsNotDialogFragment() {
		// Arrange:
		final int dialogResource = XML_DIALOG_RESOURCE_ID;
		final TestActivity mockActivity = mock(TestActivity.class);
		final FragmentManager mockFragmentManager = mock(FragmentManager.class);
		final Fragment mockFragment = mock(TestFragment.class);
		when(mockFragmentManager.findFragmentByTag(DialogXmlFactory.class.getName() + ".TAG." + dialogResource)).thenReturn(mockFragment);
		when(mockActivity.getFragmentManager()).thenReturn(mockFragmentManager);
		final UniversiContextDelegate delegate = new TestDelegate(mockActivity);
		// Act + Assert:
		assertThat(delegate.dismissXmlDialog(dialogResource), is(false));
	}

	@Test public void testDismissXmlDialogWhenPaused() {
		// Arrange:
		final UniversiContextDelegate delegate = new TestDelegate();
		delegate.setPaused(true);
		// Act + Assert:
		assertThat(delegate.dismissXmlDialog(XML_DIALOG_RESOURCE_ID), is(false));
	}

	@Test public void testIsActiveNetworkConnected() {
		// Arrange:
		final Context mockContext = mock(Context.class);
		final ConnectivityManager mockConnectivityManager = mock(ConnectivityManager.class);
		final NetworkInfo mockNetworkInfo = mock(NetworkInfo.class);
		when(mockNetworkInfo.isConnected()).thenReturn(true);
		when(mockConnectivityManager.getActiveNetworkInfo()).thenReturn(mockNetworkInfo);
		when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager);
		when(mockContext.getApplicationContext()).thenReturn(mockContext);
		final UniversiContextDelegate delegate = new TestDelegate(mockContext);
		// Act + Assert:
		assertThat(delegate.isActiveNetworkConnected(), is(true));
		assertThat(delegate.isActiveNetworkConnected(), is(true));
	}

	@Test public void testIsActiveNetworkConnectedWhenThereIsNone() {
		// Arrange:
		final Context mockContext = mock(Context.class);
		final ConnectivityManager mockConnectivityManager = mock(ConnectivityManager.class);
		when(mockConnectivityManager.getActiveNetworkInfo()).thenReturn(null);
		when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager);
		when(mockContext.getApplicationContext()).thenReturn(mockContext);
		final UniversiContextDelegate delegate = new TestDelegate(mockContext);
		// Act + Assert:
		assertThat(delegate.isActiveNetworkConnected(), is(false));
	}

	@Test public void testIsActiveNetworkConnectedWhenDisconnected() {
		// Arrange:
		final Context mockContext = mock(Context.class);
		final ConnectivityManager mockConnectivityManager = mock(ConnectivityManager.class);
		final NetworkInfo mockNetworkInfo = mock(NetworkInfo.class);
		when(mockNetworkInfo.isConnected()).thenReturn(false);
		when(mockConnectivityManager.getActiveNetworkInfo()).thenReturn(mockNetworkInfo);
		when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager);
		when(mockContext.getApplicationContext()).thenReturn(mockContext);
		final UniversiContextDelegate delegate = new TestDelegate(mockContext);
		// Act + Assert:
		assertThat(delegate.isActiveNetworkConnected(), is(false));
	}

	@SuppressWarnings("deprecation")
	@Test public void testIsNetworkConnected() {
		// Arrange:
		final Context mockContext = mock(Context.class);
		final ConnectivityManager mockConnectivityManager = mock(ConnectivityManager.class);
		final NetworkInfo mockNetworkInfo = mock(NetworkInfo.class);
		when(mockNetworkInfo.isConnected()).thenReturn(true);
		when(mockConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).thenReturn(mockNetworkInfo);
		when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager);
		when(mockContext.getApplicationContext()).thenReturn(mockContext);
		final UniversiContextDelegate delegate = new TestDelegate(mockContext);
		// Act + Assert:
		assertThat(delegate.isNetworkConnected(ConnectivityManager.TYPE_MOBILE), is(true));
	}

	@SuppressWarnings("deprecation")
	@Test public void testIsNetworkConnectedWhenThereIsNone() {
		// Arrange:
		final Context mockContext = mock(Context.class);
		final ConnectivityManager mockConnectivityManager = mock(ConnectivityManager.class);
		when(mockConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).thenReturn(null);
		when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager);
		when(mockContext.getApplicationContext()).thenReturn(mockContext);
		final UniversiContextDelegate delegate = new TestDelegate(mockContext);
		// Act + Assert:
		assertThat(delegate.isNetworkConnected(ConnectivityManager.TYPE_MOBILE), is(false));
	}

	@SuppressWarnings("deprecation")
	@Test public void testIsNetworkConnectedWhenDisconnected() {
		// Arrange:
		final Context mockContext = mock(Context.class);
		final ConnectivityManager mockConnectivityManager = mock(ConnectivityManager.class);
		final NetworkInfo mockNetworkInfo = mock(NetworkInfo.class);
		when(mockNetworkInfo.isConnected()).thenReturn(false);
		when(mockConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).thenReturn(mockNetworkInfo);
		when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager);
		when(mockContext.getApplicationContext()).thenReturn(mockContext);
		final UniversiContextDelegate delegate = new TestDelegate(mockContext);
		// Act + Assert:
		assertThat(delegate.isNetworkConnected(ConnectivityManager.TYPE_MOBILE), is(false));
	}

	@Test public void testIsViewCreated() {
		// Arrange:
		final UniversiContextDelegate delegate = new TestDelegate(application);
		// Act + Assert:
		delegate.setViewCreated(true);
		assertThat(delegate.isViewCreated(), is(true));
		delegate.setViewCreated(false);
		assertThat(delegate.isViewCreated(), is(false));
	}

	@Test public void testIsPaused() {
		// Arrange:
		final UniversiContextDelegate delegate = new TestDelegate(application);
		delegate.setPaused(true);
		// Act + Assert:
		assertThat(delegate.isPaused(), is(true));
		delegate.setPaused(false);
		assertThat(delegate.isPaused(), is(false));
	}

	@Test public void testRegisterRequest() {
		// Arrange:
		final UniversiContextDelegate delegate = new TestDelegate(application);
		delegate.registerRequest(0x00000001);
		// Act + Assert:
		assertThat(delegate.isRequestRegistered(0x00000001), is(true));
	}

	@Test public void testUnregisterRequest() {
		// Arrange:
		final UniversiContextDelegate delegate = new TestDelegate(application);
		delegate.registerRequest(0x00000001);
		delegate.unregisterRequest(0x00000001);
		// Act + Assert:
		assertThat(delegate.isRequestRegistered(0x00000001), is(false));
	}

	private static class TestDelegate extends UniversiContextDelegate {

		private Activity activity;

		TestDelegate() {
			this(mock(TestActivity.class));
		}

		TestDelegate(final Activity activity) {
			this((Context) activity);
			this.activity = activity;
		}

		TestDelegate(final Context context) {
			super(context);
		}

		@Override @NonNull DialogController instantiateDialogController() {
			return new DialogController(activity);
		}
	}
}