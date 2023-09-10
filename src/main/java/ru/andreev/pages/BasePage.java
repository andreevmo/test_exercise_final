package ru.andreev.pages;

public class BasePage {

    public static final String INPUT_BY_ID = "//input[@id='%s']";
    public static final String INPUT_BY_PLACEHOLDER = "//input[@placeholder='%s']";
    public static final String P_BY_CONTAINS_TEXT = "//p[contains(text(), '%s')]";
    public static final String DIV_BY_TEXT = "//div[text()='%s']";
    public static final String SPAN_BY_TEXT = "//span[text()='%s']";
    public static final String DIV_BY_CONTAINS_CLASS = "//div[contains(@class, '%s')]";
    public static final String DIV_BY_CLASS = "//div[@class='%s']";
}
