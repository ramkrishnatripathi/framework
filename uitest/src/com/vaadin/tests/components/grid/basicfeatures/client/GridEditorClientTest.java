/*
 * Copyright 2000-2014 Vaadin Ltd.
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
 */
package com.vaadin.tests.components.grid.basicfeatures.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.shared.ui.grid.GridConstants;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeatures;

public class GridEditorClientTest extends GridBasicClientFeaturesTest {

    private static final String[] EDIT_ROW_100 = new String[] { "Component",
            "Editor", "Edit row 100" };
    private static final String[] EDIT_ROW_5 = new String[] { "Component",
            "Editor", "Edit row 5" };

    @Before
    public void setUp() {
        openTestURL();
        selectMenuPath("Component", "Editor", "Enabled");
    }

    @Test
    public void testProgrammaticOpeningClosing() {
        selectMenuPath(EDIT_ROW_5);
        assertNotNull(getEditor());

        selectMenuPath("Component", "Editor", "Cancel edit");
        assertNull(getEditor());
        assertEquals("Row 5 edit cancelled",
                findElement(By.className("grid-editor-log")).getText());
    }

    @Test
    public void testProgrammaticOpeningWithScroll() {
        selectMenuPath(EDIT_ROW_100);
        assertNotNull(getEditor());
    }

    @Test(expected = NoSuchElementException.class)
    public void testVerticalScrollLocking() {
        selectMenuPath(EDIT_ROW_5);
        getGridElement().getCell(200, 0);
    }

    @Test
    public void testKeyboardOpeningClosing() {

        getGridElement().getCell(4, 0).click();

        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();

        assertNotNull(getEditor());

        new Actions(getDriver()).sendKeys(Keys.ESCAPE).perform();
        assertNull(getEditor());
        assertEquals("Row 4 edit cancelled",
                findElement(By.className("grid-editor-log")).getText());

        // Disable editor
        selectMenuPath("Component", "Editor", "Enabled");

        getGridElement().getCell(5, 0).click();
        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();
        assertNull(getEditor());
    }

    @Test
    public void testWidgetBinding() throws Exception {
        selectMenuPath(EDIT_ROW_100);
        WebElement editor = getEditor();

        List<WebElement> widgets = editor.findElements(By
                .className("gwt-TextBox"));

        assertEquals(GridBasicFeatures.EDITABLE_COLUMNS, widgets.size());

        assertEquals("(100, 0)", widgets.get(0).getAttribute("value"));
        assertEquals("(100, 1)", widgets.get(1).getAttribute("value"));
        assertEquals("(100, 2)", widgets.get(2).getAttribute("value"));

        assertEquals("100", widgets.get(6).getAttribute("value"));
        assertEquals("<b>100</b>", widgets.get(8).getAttribute("value"));
    }

    @Test
    public void testWithSelectionColumn() throws Exception {
        selectMenuPath("Component", "State", "Selection mode", "multi");
        selectMenuPath(EDIT_ROW_5);

        WebElement editor = getEditor();
        List<WebElement> selectorDivs = editor.findElements(By
                .cssSelector("div"));

        assertTrue("selector column cell should've been empty", selectorDivs
                .get(0).getAttribute("innerHTML").isEmpty());
        assertFalse("normal column cell shoul've had contents", selectorDivs
                .get(1).getAttribute("innerHTML").isEmpty());
    }

    @Test
    public void testSave() {
        selectMenuPath(EDIT_ROW_100);

        WebElement textField = getEditor().findElements(
                By.className("gwt-TextBox")).get(0);

        textField.clear();
        textField.sendKeys("Changed");

        WebElement saveButton = getEditor().findElement(
                By.className("v-grid-editor-save"));

        saveButton.click();

        assertEquals("Changed", getGridElement().getCell(100, 0).getText());
    }

    @Test
    public void testProgrammaticSave() {
        selectMenuPath(EDIT_ROW_100);

        WebElement textField = getEditor().findElements(
                By.className("gwt-TextBox")).get(0);

        textField.clear();
        textField.sendKeys("Changed");

        selectMenuPath("Component", "Editor", "Save");

        assertEquals("Changed", getGridElement().getCell(100, 0).getText());
    }

    @Test
    public void testCaptionChange() {
        selectMenuPath(EDIT_ROW_5);
        assertEquals("Save button caption should've been \""
                + GridConstants.DEFAULT_SAVE_CAPTION + "\" to begin with",
                GridConstants.DEFAULT_SAVE_CAPTION, getSaveButton().getText());
        assertEquals("Cancel button caption should've been \""
                + GridConstants.DEFAULT_CANCEL_CAPTION + "\" to begin with",
                GridConstants.DEFAULT_CANCEL_CAPTION, getCancelButton()
                        .getText());

        selectMenuPath("Component", "Editor", "Change Save Caption");
        assertNotEquals(
                "Save button caption should've changed while editor is open",
                GridConstants.DEFAULT_SAVE_CAPTION, getSaveButton().getText());

        getCancelButton().click();

        selectMenuPath("Component", "Editor", "Change Cancel Caption");
        selectMenuPath(EDIT_ROW_5);
        assertNotEquals(
                "Cancel button caption should've changed while editor is closed",
                GridConstants.DEFAULT_CANCEL_CAPTION, getCancelButton()
                        .getText());
    }

    public void testUneditableColumn() {
        selectMenuPath("Component", "Editor", "Edit row 5");

        assertFalse("Uneditable column should not have an editor widget",
                getGridElement().getEditor().isEditable(3));
    }

    protected WebElement getSaveButton() {
        return getEditor().findElement(By.className("v-grid-editor-save"));
    }

    protected WebElement getCancelButton() {
        return getEditor().findElement(By.className("v-grid-editor-cancel"));
    }
}
