package storage;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.TreeMap;
public class borrower {
    int value;//表示借阅的书籍
    String name,recognization,password;
    LocalDateTime regisTime;
    TreeMap<store,LocalDateTime> borrows;
    public borrower(String name,String recognization,String password){
        this.name=name;this.password=password;
        this.recognization=recognization;
        this.value=0;
        borrows=new TreeMap<>();
        this.regisTime=LocalDateTime.now();
    }
    public void borrow(store target){
        if (value<10) {
            if (target.avalibility==1) {
                target.avalibility=0;
                borrows.put(target, LocalDateTime.now());
                target.borrowTime=this.borrows.get(target);//保证完全一致
                target.borrowerID=this.name;
                System.out.println(this.recognization+" "+this.name+"在"+LocalDateTime.now()+"成功借阅书籍："+ target.title+" ID:"+target.recognization);
                this.value++;
            }
            else{
                System.out.println("借阅失败：该书籍已被借阅");
            }
        }
        else{
            System.out.println("借阅失败：用户借阅数量已经达到上限（10本）");
        }
    }
    public void returnBook(store target){
        if (borrows.containsKey(target)) {
            value--;
            borrows.remove(target);
            target.avalibility=1;
            target.borrowTime=null;
            target.borrowerID=null;
        }
        else{
            System.out.println("归还失败：该用户未借阅此书");
        }
    }
    @Override
    public String toString() {
        return "用户名：" + name + '\'' +
                ", 用户识别码='" + recognization + '\'' +
                ", 借阅书籍=" + borrows;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        borrower borrower = (borrower) o;
        return Objects.equals(name, borrower.name) && Objects.equals(recognization, borrower.recognization) && Objects.equals(borrows, borrower.borrows);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, recognization, borrows);
    }
}
