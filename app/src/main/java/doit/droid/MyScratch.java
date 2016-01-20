package doit.droid;

import java.io.IOException;

class A {
    public A() throws IOException{
        throw new IOException("zaebca");
    }
    public void doSmth(){
        System.out.println("do smth in a");
    }
}

//class B extends A {
//    @Override
//    public void doSmth(){
//        System.out.println("do smth in b");
//    }
//}

public class MyScratch {
    public static void main(String [] arg) throws IOException{
        System.out.println("Wazzzup!");
        new A();
    }

}
