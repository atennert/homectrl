/*******************************************************************************
 * Copyright 2016 Andreas Tennert
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package org.atennert.homectrl.util;

import org.atennert.homectrl.registration.DataDescription;

/**
 * This interface is implemented by classes, which receive observed events from the
 * {@link EventDistributor}.
 */
public interface EventObserver
{
    EventObserver STUB = ( description, value ) -> {};

    void notify( DataDescription description, Object value );
}
