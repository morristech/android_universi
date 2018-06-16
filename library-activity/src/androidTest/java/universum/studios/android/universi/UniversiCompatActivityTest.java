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
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.view.Menu;

import org.junit.Rule;
import org.junit.Test;

import universum.studios.android.fragment.BackPressWatcher;
import universum.studios.android.fragment.annotation.ActionBarOptions;
import universum.studios.android.fragment.annotation.ContentView;
import universum.studios.android.fragment.annotation.FragmentAnnotations;
import universum.studios.android.test.instrumented.InstrumentedTestCase;
import universum.studios.android.test.instrumented.TestFragment;
import universum.studios.android.transition.BaseNavigationalTransition;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * @author Martin Albedinsky
 */
public final class UniversiCompatActivityTest extends InstrumentedTestCase {
    
	@Rule
	public ActivityTestRule<TestActivity> ACTIVITY_RULE = new ActivityTestRule<>(TestActivity.class);

	@Override
	public void beforeTest() throws Exception {
		super.beforeTest();
		// Ensure that we have always annotations processing enabled.
		FragmentAnnotations.setEnabled(true);
	}

	@Test
	public void testOnCreateOptionsMenu() {
		final TestActivity activity = ACTIVITY_RULE.getActivity();
		final Menu mockMenu = mock(Menu.class);
		activity.onCreateOptionsMenu(mockMenu);
		verifyZeroInteractions(mockMenu);
	}

	@Test
	public void testGetFragmentControllerDefault() {
		assertThat(ACTIVITY_RULE.getActivity().getFragmentController(), is(notNullValue()));
	}

	// todo: @Test
	public void testRequestBindDataFromBackgroundThread() throws Throwable {
		final TestActivity activity = ACTIVITY_RULE.getActivity();
		activity.requestBindData();
		Thread.sleep(350);
		// fixme: this assertion does not pass
		assertThat(activity.onBindDataInvoked, is(true));
	}

	@Test
	public void testCheckSelfPermission() {
		final TestActivity activity = ACTIVITY_RULE.getActivity();
		assertThat(
				activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE),
				is(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
						context.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Process.myPid(), Process.myUid()) :
						PackageManager.PERMISSION_GRANTED
				)
		);
	}

	@Test
	@UiThreadTest
	public void testShouldShowRequestPermissionRationale() {
		final TestActivity activity = ACTIVITY_RULE.getActivity();
		assertThat(activity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE), is(false));
	}

	@Test
	public void testFinishWithNavigationalTransition() {
		final TestActivity activity = ACTIVITY_RULE.getActivity();
		activity.setNavigationalTransition(new BaseNavigationalTransition() {});
		assertThat(activity.finishWithNavigationalTransition(), is(true));
		assertThat(activity.isFinishing(), is(true));
	}

	@Test
	public void testFinishWithNavigationalTransitionWhenThereIsNoTransition() {
		final TestActivity activity = ACTIVITY_RULE.getActivity();
		assertThat(activity.finishWithNavigationalTransition(), is(false));
		assertThat(activity.isFinishing(), is(true));
	}

	@ActionBarOptions(
			homeAsUp = ActionBarOptions.HOME_AS_UP_ENABLED,
			homeAsUpIndicator = android.R.drawable.ic_delete
	)
	@ContentView(android.R.layout.simple_list_item_1)
	public static final class TestActivity extends UniversiCompatActivity {

		boolean onBindViewsInvoked, onBindDataInvoked;

		@Override
		protected void onBindViews() {
			super.onBindViews();
			this.onBindViewsInvoked = true;
		}

		@Override
		protected void onBindData() {
			super.onBindData();
			this.onBindDataInvoked = true;
		}
	}

	public static abstract class TestBackPressWatcherFragment extends TestFragment implements BackPressWatcher {
	}
}