// Copyright (C) 2021 Meituan
// All rights reserved
package springframework.context.support;

/**
 * @author Administrator
 * @version 1.0
 * @created 2021/5/18 22:33
 **/
public class OutterClass {

    private String name;
    private InnerClass innerClass = new InnerClass(){};
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

        }
    };

    public void getName() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("2222");
            }
        };
    }


    static class InnerClass {
        private String name;
    }

    class InnerClass2 {
        private String name;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

            }
        };
    }
}
