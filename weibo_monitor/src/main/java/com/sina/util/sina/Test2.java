package com.sina.util.sina;

public class Test2 {
    public static void main(String[] args) {
        //Father f = new Son("小张");
        Son son = new Son("小张");
        String name = ((Father)son).name;
        System.out.println(name);


        //1.Java在实例化对象的时候，会先对其父类(很多层)进行实例化      Son-->Father-->GrandPa-->Object    一个类默认继承Object类
        //2.如果我们没有在子类的构造方法中声明,那么会默认执行父类的无参构造方法
        //3.如果我们没有声明构造方法，那么一个类会具有一个无参构造方法 public 类名(){};如果我们声明了构造方法，那么默认的无参构造方法就会消失。我们可以声明多个构造方法，包括无参构造方法。
        //4.如果我们对一个类只声明了带参构造方法，那么其子类会报错，原因为3，找不到无参构造方法，不能实例化父类对象，所以报错。
        //5.关于这种错误的解决，两种方法
            //5.1 我们在声明父类带参构造方法后，再声明一个无参构造方法
            //5.2 我们在子类的构造方法中指明父类的构造方法， 即子类构造方法第一句写super(参数1,参数2,参数3,...)，这样父类通过带参构造方法生成，不需要无参构造方法
        //6.由此会产生方法的重写问题，原因是父类的对象先实例化，子类会对其进行覆盖。
        //7.关于参数不会产生覆盖
        //8. Father f = new Son();      可以理解为从父亲的角度来看儿子的实例对象
            //  记住两点 参数不会被覆盖(重写)，但方法会
            //   1.f.子类父类共有方法()    由于父类方法被子类覆盖(重写)，所以实际调用子类方法
            //   2.f.父类独有方法()       没有被覆盖，实际调用父类
            //   3.f.子类独有方法()       从父亲角度来看，没有这个方法()
            //   4.f.父类独有属性()       对应父类值
            //   5.f.子类独有属性()       报错(子类在第二层，你却在第一层)
            //   6.f.子类父类共同属性()    对应父类值    属性不会被覆盖
        //9.由此产生的思考
            //1 Son s = new Son()   s.super.name是不是应该对应着父类属性  x 原因不能这样子写
            //(Father)s.name  是不是应该对应着父类属性  x 不能这样子写  原因 这个的意思是我们把s.name这个String型转换为Father，显然不对
            //((Father)s).name  √
            //在运行上述程序时发现输出为null,这是什么原因？   原因在Son的构造方法中，原因在5.2
            //
    }
}

class GrandPa{
    protected String name;

    public GrandPa(){
        System.out.println("张大爷默认构造方法执行");
    }


    public GrandPa(String name) {
        this.name = "张大爷";
        System.out.println("张大爷带参构造方法执行");
    }
    public void hello(){
        System.out.println("我是张大爷");
    }
}
class Father extends GrandPa{
    protected String name;

    public Father(){
        System.out.println("默认张爸爸构造方法执行");
    }


    public Father(String name) {
        this.name = "张爸爸";
        System.out.println("张爸爸带参构造方法执行");
    }
    public void hello(){
        System.out.println("我是张爸爸");
    }
}

class Son extends Father{
    protected String age;
    protected String name;
    public Son(String name) {
        //super("xiaozhang");
        super();
        System.out.println("子类构造方法执行");
        this.name = "小张";
    }
    public void hello(){
        System.out.println("我是小张");
    }
    public void hello2(){
        System.out.println("小张独有");
    }
}