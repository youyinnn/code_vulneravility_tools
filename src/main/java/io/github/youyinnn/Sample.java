package io.github.youyinnn;

/**
 * @author yinnnyou
 */
public class Sample {
    private String sentence;
    private String label;

    public Sample() {
    }

    public Sample(String sentence, String label) {
        this.sentence = sentence;
        this.label = label;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "{\"Sample\":{"
                + "\"sentence\":\"" + sentence + "\""
                + ", \"label\":\"" + label + "\""
                + "}}";
    }
}
