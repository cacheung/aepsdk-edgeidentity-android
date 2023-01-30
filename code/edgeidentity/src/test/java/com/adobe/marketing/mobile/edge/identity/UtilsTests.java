/*
  Copyright 2021 Adobe. All rights reserved.
  This file is licensed to you under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under
  the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
  OF ANY KIND, either express or implied. See the License for the specific language
  governing permissions and limitations under the License.
*/

package com.adobe.marketing.mobile.edge.identity;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class UtilsTests {

	@Test
	public void test_isNullOrEmpty_nullList() {
		assertTrue(Utils.isNullOrEmpty((List<?>) null));
	}

	@Test
	public void test_isNullOrEmpty_emptyList() {
		assertTrue(Utils.isNullOrEmpty(Collections.EMPTY_LIST));
	}

	@Test
	public void test_isNullOrEmpty_nonEmptyNonNullList() {
		assertFalse(Utils.isNullOrEmpty(Arrays.asList("A", 1, true)));
	}
}
