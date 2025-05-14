package storage;

import java.time.LocalDateTime;
import java.util.Objects;

public class store implements Comparable<store>{
    int type,avalibility,recognization,size,num_same;//type=0则为书，type=1则为CD，type=2则为磁盘，此时size=0
    LocalDateTime year,borrowTime;
    String author ,title,borrowerID;
    public store(String author,String title,int size,int type,int recognization){
        this.type=type;
        this.avalibility=1;
        this.title=title;
        this.recognization=recognization;
        this.year=LocalDateTime.now();
        this.author=author;
        this.size=size;
    }
    public void printMessage(){//极致细化
        if (this.type==0) {
            System.out.print("书籍："+this.title+" 作者："+this.author+" 页数："+this.size);
        }
        else if (this.type==1) {
            System.out.print("CD："+this.title+" 作者"+this.author);
        }
        else if(this.type==2){
            System.out.print("磁盘："+this.title+" 作者："+this.author);
        }
        else{
            System.out.print("其他类资料："+this.title+" 作者："+this.author);
        }
        System.out.print(" 识别码："+this.recognization+" 入库日期："+this.year+" 是否可被借阅：");
        if (this.avalibility==0) {
            System.out.print("否\n借阅者ID："+this.borrowerID+"借阅时间"+this.borrowTime+"\n");
        }
        else{
            System.out.print("是\n");
        }
    }
    @Override
    public int compareTo(store other) {
        return Integer.compare(this.recognization, other.recognization);
    }
    @Override
    public String toString() {//极简风格
        return "store{" +
                "author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", size='" + size + '\'' +
                ", type=" + type +
                ", recognization=" + recognization +
                ", year=" + year +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        store store = (store) o;
        return type == store.type && recognization == store.recognization && year == store.year && Objects.equals(author, store.author) && Objects.equals(title, store.title) && Objects.equals(size, store.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, title, size, type, recognization, year);
    }
}