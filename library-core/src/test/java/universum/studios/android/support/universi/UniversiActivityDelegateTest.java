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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import org.hamcrest.core.Is;
import org.junit.Test;

import universum.studios.android.support.fragment.manage.FragmentController;
import universum.studios.android.support.fragment.manage.FragmentFactory;
import universum.studios.android.support.test.local.RobolectricTestCase;
import universum.studios.android.support.test.local.TestFragment;
import universum.studios.android.transition.BaseNavigationalTransition;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Martin Albedinsky
 */
public final class UniversiActivityDelegateTest extends RobolectricTestCase {

	@Test public void testInstantiation() {
		// Act:
		final FragmentActivity mockActivity = mock(FragmentActivity.class);
		final UniversiActivityDelegate delegate = new UniversiActivityDelegate(mockActivity);
		// Act:
		assertThat(delegate.context, Is.<Context>is(mockActivity));
	}

	@Test public void testInstantiateDialogController() {
		// Arrange:
		final UniversiActivityDelegate delegate = new UniversiActivityDelegate(mock(FragmentActivity.class));
		// Act + Assert:
		assertThat(delegate.instantiateDialogController(), is(notNullValue()));
	}

	@Test public void testStartLoader() {
		// Arrange:
		final Loader mockLoader = mock(Loader.class);
		final LoaderManager.LoaderCallbacks mockCallbacks = mock(LoaderManager.LoaderCallbacks.class);
		final LoaderManager mockLoaderManager = mock(LoaderManager.class);
		when(mockLoaderManager.initLoader(1, null, mockCallbacks)).thenReturn(mockLoader);
		when(mockLoaderManager.getLoader(1)).thenReturn(null);
		final FragmentActivity mockActivity = mock(FragmentActivity.class);
		when(mockActivity.getSupportLoaderManager()).thenReturn(mockLoaderManager);
		when(mockCallbacks.onCreateLoader(1, null)).thenReturn(mockLoader);
		final UniversiActivityDelegate delegate = new UniversiActivityDelegate(mockActivity);
		// Act:
		assertThat(delegate.startLoader(1, null, mockCallbacks), is(mockLoader));
		// Assert:
		verify(mockLoaderManager).getLoader(1);
		verify(mockLoaderManager).initLoader(1, null, mockCallbacks);
		verifyNoMoreInteractions(mockLoaderManager);
	}

	@SuppressWarnings("unchecked")
	@Test public void testStartLoaderWhenAlreadyStarted() {
		// Arrange:
		final Loader mockLoader = mock(Loader.class);
		final LoaderManager.LoaderCallbacks mockCallbacks = mock(LoaderManager.LoaderCallbacks.class);
		final LoaderManager mockLoaderManager = mock(LoaderManager.class);
		when(mockLoaderManager.restartLoader(1, null, mockCallbacks)).thenReturn(mockLoader);
		when(mockLoaderManager.getLoader(1)).thenReturn(mockLoader);
		final FragmentActivity mockActivity = mock(FragmentActivity.class);
		when(mockActivity.getSupportLoaderManager()).thenReturn(mockLoaderManager);
		final UniversiActivityDelegate delegate = new UniversiActivityDelegate(mockActivity);
		// Act:
		assertThat(delegate.startLoader(1, null, mockCallbacks), is(mockLoader));
		// Assert:
		verify(mockLoaderManager).getLoader(1);
		verify(mockLoaderManager).restartLoader(1, null, mockCallbacks);
		verifyNoMoreInteractions(mockLoaderManager);
	}

	@SuppressWarnings("unchecked")
	@Test public void testDestroyLoader() {
		// Arrange:
		final FragmentActivity mockActivity = mock(FragmentActivity.class);
		final LoaderManager mockLoaderManager = mock(LoaderManager.class);
		when(mockActivity.getSupportLoaderManager()).thenReturn(mockLoaderManager);
		final UniversiActivityDelegate delegate = new UniversiActivityDelegate(mockActivity);
		// Act:
		delegate.destroyLoader(1);
		// Assert:
		verify(mockLoaderManager).destroyLoader(1);
		verifyNoMoreInteractions(mockLoaderManager);
	}

	@Test public void testNavigationalTransition() {
		// Arrange:
		final FragmentActivity mockActivity = mock(FragmentActivity.class);
		final UniversiActivityDelegate delegate = new UniversiActivityDelegate(mockActivity);
		final BaseNavigationalTransition mockTransition = mock(BaseNavigationalTransition.class);
		// Act + Assert:
		delegate.setNavigationalTransition(mockTransition);
		assertThat(delegate.getNavigationalTransition(), is(mockTransition));
		verify(mockTransition).configureIncomingTransitions(mockActivity);
	}

	@Test public void testSetNullNavigationalTransition() {
		// Arrange:
		final FragmentActivity mockActivity = mock(FragmentActivity.class);
		final UniversiActivityDelegate delegate = new UniversiActivityDelegate(mockActivity);
		// Act:
		delegate.setNavigationalTransition(null);
		// Assert:
		assertThat(delegate.getNavigationalTransition(), is(nullValue()));
	}

	@Test public void testFinishWithNavigationalTransition() {
		// Arrange:
		final FragmentActivity mockActivity = mock(FragmentActivity.class);
		final UniversiActivityDelegate delegate = new UniversiActivityDelegate(mockActivity);
		final BaseNavigationalTransition mockTransition = mock(BaseNavigationalTransition.class);
		delegate.setNavigationalTransition(mockTransition);
		// Act + Assert:
		assertThat(delegate.finishWithNavigationalTransition(), is(true));
	}

	@Test public void testFinishWithNavigationalTransitionWithoutAttachedTransition() {
		// Arrange:
		final FragmentActivity mockActivity = mock(FragmentActivity.class);
		final UniversiActivityDelegate delegate = new UniversiActivityDelegate(mockActivity);
		// Act + Assert:
		assertThat(delegate.finishWithNavigationalTransition(), is(false));
	}

	@Test public void testFragmentController() {
		// Arrange:
		final FragmentActivity mockActivity = mock(FragmentActivity.class);
		when(mockActivity.getSupportFragmentManager()).thenReturn(mock(FragmentManager.class));
		final UniversiActivityDelegate delegate = new UniversiActivityDelegate(mockActivity);
		final FragmentFactory mockFactory = mock(FragmentFactory.class);
		final FragmentController mockController = mock(FragmentController.class);
		delegate.setFragmentFactory(mockFactory);
		// Act + Assert:
		delegate.setFragmentController(mockController);
		assertThat(delegate.getFragmentController(), is(mockController));
		verify(mockController).setFactory(mockFactory);
		verifyNoMoreInteractions(mockController);
	}

	@Test public void testSetNullFragmentController() {
		// Arrange:
		final FragmentActivity mockActivity = mock(FragmentActivity.class);
		when(mockActivity.getSupportFragmentManager()).thenReturn(mock(FragmentManager.class));
		final UniversiActivityDelegate delegate = new UniversiActivityDelegate(mockActivity);
		// Act:
		delegate.setFragmentController(null);
		// Assert:
		assertThat(delegate.getFragmentController(), is(notNullValue()));
	}

	@Test public void testFragmentFactory() {
		// Arrange:
		final FragmentActivity mockActivity = mock(FragmentActivity.class);
		when(mockActivity.getSupportFragmentManager()).thenReturn(mock(FragmentManager.class));
		final UniversiActivityDelegate delegate = new UniversiActivityDelegate(mockActivity);
		final FragmentFactory mockFactory = mock(FragmentFactory.class);
		// Act + Assert:
		delegate.setFragmentFactory(mockFactory);
		assertThat(delegate.getFragmentFactory(), is(mockFactory));
		verifyZeroInteractions(mockFactory);
	}

	@Test public void testFindCurrentFragment() {
		// Arrange:
		final FragmentController mockController = mock(FragmentController.class);
		final Fragment mockFragment = mock(TestFragment.class);
		when(mockController.findCurrentFragment()).thenReturn(mockFragment);
		final FragmentActivity mockActivity = mock(FragmentActivity.class);
		final UniversiActivityDelegate delegate = new UniversiActivityDelegate(mockActivity);
		delegate.setFragmentController(mockController);
		// Act + Assert:
		assertThat(delegate.findCurrentFragment(), is(mockFragment));
	}

	@Test public void testFindCurrentFragmentWithoutControllerInitialized() {
		// Arrange:
		final FragmentManager mockManager = mock(FragmentManager.class);
		when(mockManager.getBackStackEntryCount()).thenReturn(1);
		final FragmentActivity mockActivity = mock(FragmentActivity.class);
		when(mockActivity.getSupportFragmentManager()).thenReturn(mockManager);
		final UniversiActivityDelegate delegate = new UniversiActivityDelegate(mockActivity);
		// Act + Assert:
		assertThat(delegate.findCurrentFragment(), is(nullValue()));
	}

	@Test public void testPopFragmentsBackStack() {
		// Arrange:
		final FragmentManager mockManager = mock(FragmentManager.class);
		when(mockManager.getBackStackEntryCount()).thenReturn(1);
		final FragmentActivity mockActivity = mock(FragmentActivity.class);
		when(mockActivity.getSupportFragmentManager()).thenReturn(mockManager);
		final UniversiActivityDelegate delegate = new UniversiActivityDelegate(mockActivity);
		// Act + Assert:
		assertThat(delegate.popFragmentsBackStack(), is(true));
		verify(mockManager, times(1)).getBackStackEntryCount();
		verify(mockManager, times(1)).popBackStack();
		verifyNoMoreInteractions(mockManager);
	}

	@Test public void testPopFragmentsBackStackOnEmptyStack() {
		// Arrange:
		final FragmentManager mockManager = mock(FragmentManager.class);
		when(mockManager.getBackStackEntryCount()).thenReturn(0);
		final FragmentActivity mockActivity = mock(FragmentActivity.class);
		when(mockActivity.getSupportFragmentManager()).thenReturn(mockManager);
		final UniversiActivityDelegate delegate = new UniversiActivityDelegate(mockActivity);
		// Act + Assert:
		assertThat(delegate.popFragmentsBackStack(), is(false));
		verify(mockManager).getBackStackEntryCount();
		verifyNoMoreInteractions(mockManager);
	}
}