/*
 * EID.java
 *
 * Copyright (C) 2011 IBR, TU Braunschweig
 *
 * Written-by: Johannes Morgenroth <morgenroth@ibr.cs.tu-bs.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package de.tubs.ibr.dtn.api;

import android.os.Parcelable;

/**
 * Represents an Endpoint ID
 * <p>
 * An endpoint is a set of zero or more bundle nodes that is identified by a
 * text string that takes the form of a Uniform Resource Identifier
 * @see https://tools.ietf.org/html/rfc5050#section-4.4
*/
public interface EID extends Parcelable {
	public String toString();
}
