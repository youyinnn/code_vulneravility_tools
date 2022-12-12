package demo.test;

import test1.*;
import test2.TestTool;

public class TestClass {
    
    private String name;
    public Integer age;
    
    public TestClass() {
        // empty
    }
    
    public TestClass(String name, Integer age) {
        this.name = name;
        this.age = age;
    }
    
    public String getName() {
        return this.name;
    }
    
    public static void main(Stirng[] args) {
        TestClass tc = new TestClass("Jack", 26);
        System.out.println("Hello" + tc.getName() + ", age: " + tc.age);
    }
}
