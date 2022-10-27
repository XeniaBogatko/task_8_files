package com.bogatkok;

import com.bogatkok.model.Student;
import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FileParseTest {

    ClassLoader cl = FileParseTest.class.getClassLoader();

    @Test
    void pdfTest() throws IOException {
        //PDF pdf = new PDF(URI.create("file:///Users/xenia/Documents/QA%20GURU/qa_guru_files/src/test/resources/q.pdf"));
        try (InputStream is = cl.getResourceAsStream("q.pdf")){
            PDF pdf = new PDF(is);
            assertThat(pdf.numberOfPages).isEqualTo(3);
        }
    }

    @Test
    void xlsTest() throws IOException {
        try (InputStream is = cl.getResourceAsStream("e.xlsx")){
            XLS xls = new XLS(is);
            assertThat(xls.excel.getSheetAt(0)
                    .getRow(0)
                    .getCell(0)
                    .getStringCellValue()).isEqualTo("XLSX test file");
        }
    }

    @Test
    void csvTest() throws IOException, CsvException {
        try (InputStream is = cl.getResourceAsStream("c.csv");
             CSVReader reader = new CSVReader(new InputStreamReader(is))) {
            List<String[]> content = reader.readAll();
            String[] row = content.get(0);
            assertThat(row[0]).isEqualTo("Number");
            assertThat(row[1]).isEqualTo("Footnote");
        }
    }

    @Test
    void zipTest() throws Exception {
        try (InputStream is = cl.getResourceAsStream("archiv.zip");
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipFile zipFile = new ZipFile("src/test/resources/archiv.zip");
            ZipEntry entry = null;
            while ((entry = zis.getNextEntry()) != null) {
                String entryName = entry.getName();
                if (entryName.contains("csv")) {
                    try (InputStream inputStream = zipFile.getInputStream(entry);
                         CSVReader reader = new CSVReader(new InputStreamReader(inputStream));) {
                        List<String[]> content = reader.readAll();
                        String[] row = content.get(0);
                        assertThat(row[0]).isEqualTo("Number");
                        assertThat(row[1]).isEqualTo("Footnote");
                    }
                }
                if (entryName.contains("pdf")) {
                    try (InputStream inputStream = zipFile.getInputStream(entry)) {
                        PDF pdf = new PDF(inputStream);
                        assertThat(pdf.numberOfPages).isEqualTo(3);
                    }
                }
                if (entryName.contains("xlsx")) {
                    try (InputStream inputStream = zipFile.getInputStream(entry)) {
                        XLS xls = new XLS(inputStream);
                        assertThat(xls.excel.getSheetAt(0)
                                .getRow(0)
                                .getCell(0)
                                .getStringCellValue()).isEqualTo("XLSX test file");
                    }
                }
            }
        }
    }

    @Test
    void jsonTest() throws IOException {
        try (InputStream is = cl.getResourceAsStream("student.json")){
            ObjectMapper objectMapper = new ObjectMapper();
            Student student = objectMapper.readValue(is, Student.class);
            assertThat(student.name).isEqualTo("Anna");
            assertThat(student.surname).isEqualTo("Karenina");
            assertThat(student.school).isEqualTo("Harvard");
            assertThat(student.lastSessionPassed).isTrue();
            assertThat(student.yearOfStudy).isEqualTo(2);
            assertThat(student.subjects).isEqualTo(
                    Arrays.asList("Math", "English", "Data Base", "Algorithms", "Computer programming"));
        }
    }
}
