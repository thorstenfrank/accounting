/*
 *  Copyright 2015 Thorsten Frank (accounting@tfsw.de).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package de.tfsw.accounting.ui.util;

import org.eclipse.swt.widgets.Button;

import de.tfsw.accounting.util.TimeFrame;

/**
 * Client or callback interface used by {@link MonthAndYearSelector} instances.
 * 
 * <p>
 * Implementations provide the model data via {@link #getTimeFrame()} and are notified of changes made by users by
 * selections through {@link #timeFrameChanged()}.
 * </p>
 * 
 * @author Thorsten Frank
 *
 */
public interface MonthAndYearSelectorCallback {

	/**
	 * Use this key to set up a shortcut/preset button.
	 * <p>
	 * See {@link MonthAndYearSelector#adaptTimeFrameTypeSelector(Button)} for details.
	 * </p>
	 */
	public static final String TIME_FRAME_TYPE_KEY = "TimeFrameType"; //$NON-NLS-1$
	
	/**
	 * The value returned by this method represents the earliest value available in the year combo.
	 *  
	 * @return the oldest necessary year in the combo selection
	 */
	int getOldestYear();
	
	/**
	 * Returns the model data being manipulated through combo and preset buttons.
	 * 
	 * @return the implementation's {@link TimeFrame}
	 */
	TimeFrame getTimeFrame();
	
	/**
	 * Called whenever a user has made changes to the {@link TimeFrame} supplied via {@link #getTimeFrame()} for 
	 * further processing by the client. Note that at this time, the {@link TimeFrame} will already have been
	 * changed to reflect the user's selection.
	 */
	void timeFrameChanged();
}