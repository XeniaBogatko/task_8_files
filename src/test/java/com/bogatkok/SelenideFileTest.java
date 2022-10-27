package com.bogatkok;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;

public class SelenideFileTest {

    /* в случае скачивания файла по кнопке
    static {
        Configuration.fileDownload = FileDownloadMode.PROXY;
    }*/

    @Test
    void selenideFileDownloadTest() throws IOException {
        //скачивание файла с атрибутом href
        open("https://github.com/junit-team/junit5/blob/main/README.md");
        //абстракция, которая определяет путь к файлу
        File downloadedFile = $("#raw-url").download();

        //file to string apache commons заменяет весь try ниже
        //String contents = FileUtils.readFileToString(downloadedFile, StandardCharsets.UTF_8);

        //чтение файла побайтово
        try (InputStream is = new FileInputStream(downloadedFile)){
            byte[] fileSource = is.readAllBytes();
            String fileContent = new String(fileSource, StandardCharsets.UTF_8);
            assertThat(fileContent).contains("This repository is the home of the next generation of JUnit");
        }
        //чтение посимвольно Reader
    }

    @Test
    void uploadFile() throws Exception {
        open("https://fineuploader.com/demos.html");
        $("input[type = 'file']").uploadFromClasspath("cat.jpeg");
        $("div.qq-file-info").shouldHave(text("cat.jpeg"));
    }
}
