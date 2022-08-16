package io.github.youyinnn;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.opencsv.exceptions.CsvException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author yinnnyou
 */
public class Main {

    public static void main(String[] args) throws URISyntaxException, IOException, CsvException {

        String datasetName = "juliet";

        getImportOnlyCsv(getSamplesFromCSV(datasetName + "/train.csv"), datasetName + "_train_i.csv");
        getImportOnlyCsv(getSamplesFromCSV(datasetName + "/dev.csv"), datasetName + "_dev_i.csv");

        getCommentOnlyCsv(getSamplesFromCSV(datasetName + "/train.csv"), datasetName + "_train_c.csv");
        getCommentOnlyCsv(getSamplesFromCSV(datasetName + "/dev.csv"), datasetName + "_dev_c.csv");

        getMainCodeOnlyCsv(getSamplesFromCSV(datasetName + "/train.csv"), datasetName + "_train_m.csv");
        getMainCodeOnlyCsv(getSamplesFromCSV(datasetName + "/dev.csv"), datasetName + "_dev_m.csv");

        getMainCodeAndImportCsv(getSamplesFromCSV(datasetName + "/train.csv"), datasetName + "_train_mi.csv");
        getMainCodeAndImportCsv(getSamplesFromCSV(datasetName + "/dev.csv"), datasetName + "_dev_mi.csv");

        getMainCodeAndCommentCsv(getSamplesFromCSV(datasetName + "/train.csv"), datasetName + "_train_mc.csv");
        getMainCodeAndCommentCsv(getSamplesFromCSV(datasetName + "/dev.csv"), datasetName + "_dev_mc.csv");

        getImportAndCommentCsv(getSamplesFromCSV(datasetName + "/train.csv"), datasetName + "_train_ic.csv");
        getImportAndCommentCsv(getSamplesFromCSV(datasetName + "/dev.csv"), datasetName + "_dev_ic.csv");
    }

    private static void getCommentOnlyCsv(ArrayList<Sample> sa, String fileName) {
        getOnlyComment(sa);
        writeCsv(sa, fileName);
    }

    private static void getImportOnlyCsv(ArrayList<Sample> sa, String fileName) {
        getOnlyImport(sa);
        writeCsv(sa, fileName);
    }

    private static void getMainCodeOnlyCsv(ArrayList<Sample> sa, String fileName) {
        removeImport(sa);
        removeComment(sa);
        writeCsv(sa, fileName);
    }

    private static void getMainCodeAndCommentCsv(ArrayList<Sample> sa, String fileName) {
        removeImport(sa);
        writeCsv(sa, fileName);
    }

    private static void getMainCodeAndImportCsv(ArrayList<Sample> sa, String fileName) {
        removeComment(sa);
        writeCsv(sa, fileName);
    }

    private static void getImportAndCommentCsv(ArrayList<Sample> sa, String fileName) {
        getImportAndComment(sa);
        writeCsv(sa, fileName);
    }

    private static void getOnlyComment(ArrayList<Sample> sa) {
        for (Sample sample : sa) {
            String code = sample.getSentence();
            CompilationUnit cu = StaticJavaParser.parse(code);

            StringJoiner sj = new StringJoiner("");
            for (Comment comment : cu.getAllComments()) {
                sj.add(comment.toString());
            }
            String newCode = sj.toString();
            sample.setSentence(newCode);
        }
    }

    private static void getOnlyImport(ArrayList<Sample> sa) {
        for (Sample sample : sa) {
            String code = sample.getSentence();
            CompilationUnit cu = StaticJavaParser.parse(code);

            StringJoiner sj = new StringJoiner("");
            for (ImportDeclaration importDeclaration : cu.getImports()) {
                sj.add(importDeclaration.toString());
            }
            String newCode = sj.toString();
            sample.setSentence(newCode);
        }
    }

    private static void getImportAndComment(ArrayList<Sample> sa) {
        for (Sample sample : sa) {
            String code = sample.getSentence();
            CompilationUnit cu = StaticJavaParser.parse(code);

            StringJoiner sj = new StringJoiner("");

            for (Comment comment : cu.getAllComments()) {
                sj.add(comment.toString());
            }
            for (ImportDeclaration importDeclaration : cu.getImports()) {
                sj.add(importDeclaration.toString());
            }

            String newCode = sj.toString();
            sample.setSentence(newCode);
        }
    }

    private static void removeComment(ArrayList<Sample> sa) {
        // remove comments
        for (Sample sample : sa) {
            String code = sample.getSentence();
            CompilationUnit cu = StaticJavaParser.parse(code);

            while (cu.getComment().isPresent()) {
                cu.getAllComments().get(0).remove();
                cu = StaticJavaParser.parse(cu.toString());
            }
            String newCode = cu.toString();
            sample.setSentence(newCode);
        }
    }

    private static void removeImport(ArrayList<Sample> sa) {
        // remove comments
        for (Sample sample : sa) {
            String code = sample.getSentence();
            CompilationUnit cu = StaticJavaParser.parse(code);

            while (cu.getImports().size() > 0) {
                cu.getImports().get(0).remove();
            }

            String newCode = cu.toString();
            sample.setSentence(newCode);
        }
    }

    private static void writeCsv(ArrayList<Sample> sa, String fileName) {
        ICsvBeanWriter beanWriter = null;
        try
        {
            beanWriter = new CsvBeanWriter(new FileWriter("data" + FileSystems.getDefault().getSeparator() + fileName), CsvPreference.STANDARD_PREFERENCE);
            final String[] header = new String[] { "sentence", "label", };

            // write the header
            beanWriter.writeHeader(header);

            // write the beans data
            for (Sample s : sa) {
                beanWriter.write(s, header);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }  finally {
            try {
                Objects.requireNonNull(beanWriter).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static File getFileFromResource(String fileName) throws URISyntaxException {

        ClassLoader classLoader = Main.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return new File(resource.toURI());
        }
    }

    private static ArrayList<Sample> getSamplesFromCSV(String fileName) throws IOException, URISyntaxException {
        File file = getFileFromResource(fileName);
        ArrayList<Sample> sa = new ArrayList<>();
        try(ICsvBeanReader beanReader
                    = new CsvBeanReader(new FileReader(file), CsvPreference.STANDARD_PREFERENCE))
        {
            // the header elements are used to map the values to the bean
            final String[] headers = beanReader.getHeader(true);
            Sample s;
            while ((s = beanReader.read(Sample.class, headers)) != null) {
                sa.add(s);
            }
        }
        return sa;
    }
}
