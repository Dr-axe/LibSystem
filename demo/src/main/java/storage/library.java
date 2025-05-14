package storage;
import java.util.*;
/*先不做移除，假定该图书馆只进行借阅不进行出售且不移除书籍且用户不出现重名 */
public class library {
    String[] identifier={"书籍","光盘","磁带","其他类资料"};
    TreeSet<store> list_reconization;
    HashMap<String,ArrayList<store>> list_title;
    HashMap<String,borrower> registerList_name;
    HashMap<String,borrower> registerList_recognization;
    private DatabaseManager dbManager;

    public library() {
        list_reconization = new TreeSet<>();
        list_title = new HashMap<>();
        registerList_name = new HashMap<>();
        registerList_recognization = new HashMap<>();
        dbManager = new DatabaseManager();
        loadDataFromDatabase();
    }
    private void loadDataFromDatabase() {
        List<borrower> borrowers = dbManager.getAllBorrowers();
        for (borrower borrower : borrowers) {
            registerList_name.put(borrower.name, borrower);
            registerList_recognization.put(borrower.recognization, borrower);
        }

        List<store> stores = dbManager.getAllStores();
        for (store store : stores) {
            list_reconization.add(store);
            if (!list_title.containsKey(store.title)) {
                list_title.put(store.title, new ArrayList<>());
            }
            list_title.get(store.title).add(store);
        }
    }
    int backType(String tp){
        if (tp.equals("书籍")) {
            return 0;
        }
        else if(tp.equals("CD")||tp.equals("cd")||tp.equals("光盘")){
            return 1;
        }
        else if (tp.equals("磁盘")) {
            return 2;
        }
        else {return 3;}
    }
    public void addBook(String author,String title,int size,int type,int recognization){
        store newBook=new store(author, title, size, type, recognization);
        list_reconization.add(newBook);
        if (!list_title.containsKey(title)) {
            list_title.put(title, new ArrayList<>());
        }
        list_title.get(title).add(newBook);
        dbManager.saveStore(newBook);
        System.out.println("已经加入"+identifier[newBook.type]+"："+newBook.title+" 作者："+newBook.author);
    }
    public void addBook_menu(){
        Scanner scanner=new Scanner(System.in);
        System.out.println("输入作品类型（书籍，CD,磁盘，其他类资料）");
        String tp=scanner.next();int type=backType(tp);
        int size=0;if (type==0) {System.out.print("\n输入书籍页数：");size=scanner.nextInt();}
        System.out.print("\n输入作品标题：");String title=scanner.next();
        System.out.print("\n输入作品创作者：");String author=scanner.next();
        System.out.print("\n输入物品识别码：");int recognization=scanner.nextInt();
        System.out.print("\n即将加入"+identifier[type]+"："+title+" 作者："+author+" 识别码："+recognization);
        if(type==0){System.out.print(" 页数："+size);}
        System.out.println("按Enter确认加入，0取消加入，1重新加入");
        scanner.nextLine();
        String input = scanner.nextLine();
        if ("0".equals(input)) {System.out.println("已取消，自动返回上级菜单");return;}
        else if("1".equals(input)){addBook_menu();}
        else{addBook(author, title, size, type, recognization);}
    }
    public void addRegister(String name,String recognization,String password) {
        borrower newBorrower = new borrower(name, recognization,password);
        if (!registerList_name.containsKey(name)) {
            registerList_name.put(name, newBorrower);
        }
        if (!registerList_recognization.containsKey(recognization)) {
            registerList_recognization.put(recognization, newBorrower);
        }
        System.out.println("成功注册：" + name + " 识别码：" + recognization);
        dbManager.saveBorrower(newBorrower);
    }
    public void addRegister_menu(){
        Scanner scanner=new Scanner(System.in);
        System.out.print("\n输入用户名:");
        String name=scanner.next();
        while(registerList_name.containsKey(name)){
            System.out.println("用户名已存在,请重新输入");
            name=scanner.next();
        }
        System.out.print("\n输入识别码:");
        String recognization=scanner.next();  
        while (registerList_recognization.containsKey(recognization)) {
            System.out.println("识别码重复,请重新输入");
            recognization=scanner.next();
        }
        scanner.nextLine();
        System.out.println("是否确认注册 用户名 ： "+name+" 识别码 ： "+recognization);
        System.out.println("按Enter确认，0取消，1重来");
        String input = scanner.nextLine();
        if ("0".equals(input)) {System.out.println("已取消，自动返回上级菜单");return;}
        else if("1".equals(input)){addRegister_menu();}
        else{
            System.out.println("请牢记自己的信息，接下来请输入你的密码");
            String password=scanner.next();
            System.out.println("再次确认密码");
            String checkPassword=scanner.next();
            while (!password.equals(checkPassword)) {
                System.out.println("两次密码不一致，请重新输入密码");password=scanner.next();
                System.out.println("再次确认密码");checkPassword=scanner.next();
            }
            System.out.println("密码一致，确认完毕");
            addRegister(name, recognization,password);}
    }
    public void borrowBook(borrower a,store b){
        a.borrow(b);
        dbManager.saveStore(b);
        dbManager.saveBorrow(a, b);
    }
    public void returnBook(borrower a,store b){
        a.returnBook(b);
        dbManager.saveStore(b);
    }
    public void printListReconization() {
        int count = 0;
        Scanner scanner = new Scanner(System.in);
        for (store book : list_reconization) {
            book.printMessage();
            count++;
            if (count % 20 == 0) {
                System.out.println("按Enter继续查询下一页（20个），按0退出查询");
                String input = scanner.nextLine();
                if ("0".equals(input)) {
                    break;
                }
            }
        }
    }

    // 查询 list_reconization 表
    public void queryListReconization() {
        int count = 1;
        Scanner scanner = new Scanner(System.in);
        System.out.println("输入查找目标的识别码");
        int recognization=scanner.nextInt();
        store target = new store(null, null, 0, 0, recognization);
        store startElement = list_reconization.ceiling(target);
        if (startElement != null) {
            // 从找到的元素开始遍历
            Iterator<store> iterator = list_reconization.tailSet(startElement).iterator();
            iterator.next().printMessage();
            scanner.nextLine();
            System.out.println("按Enter继续查询下一页（每页最多20个），按0退出查询");
            String input = scanner.nextLine();
            if ("0".equals(input)) {
                return;
            }
            while (iterator.hasNext()) {
                count++;
                iterator.next().printMessage();
                if (count % 20 == 0) {
                    System.out.println("按Enter继续查询下一页（每页最多20个），按0退出查询");
                    input = scanner.nextLine();
                    if ("0".equals(input)) {
                        break;
                    }
                }
            }
        } else {
            System.out.println("未找到符合条件的物品");
        }
    }

    // 输出 list_title 表
    public void printListTitle() {
        int count = 0;
        Scanner scanner = new Scanner(System.in);
        for (Map.Entry<String, ArrayList<store>> entry : list_title.entrySet()) {
            for (store book : entry.getValue()) {
                book.printMessage();
                count++;
                if (count % 20 == 0) {
                    System.out.println("按Enter继续查询下一页（20个），按0退出查询");
                    String input = scanner.nextLine();
                    if ("0".equals(input)) {
                        break;
                    }
                }
            }
        }
    }
    // 查询 list_title 表
    public void queryListTitle() {
        int count = 0;
        Scanner scanner = new Scanner(System.in);
        System.out.println("输入查找目标标题：");
        String target=scanner.next();
        ArrayList<store> startElement = list_title.get(target);
        if (startElement != null) {
            // 从找到的元素开始遍历
            Iterator<store> iterator =startElement.iterator(); 
            while (iterator.hasNext()) {
                count++;
                iterator.next().printMessage();
                if (count % 20 == 0) {
                    System.out.println("按Enter继续查询下一页（20个），按0退出查询");
                    scanner.nextLine();
                    String input = scanner.nextLine();
                    if ("0".equals(input)) {
                        break;
                    }
                }
            }
        } else {
            System.out.println("精确查询未找到目标");
        }
        System.out.println("按Enter继续进行模糊查询，按0退出查询");
        scanner.nextLine();
        String input = scanner.nextLine();
        if ("0".equals(input)) {
            return;
        }
        count=0;
        System.out.println("以下是模糊查询的结果");
        boolean startTraversal = false;
        Iterator<Map.Entry<String, ArrayList<store>>> iterator = list_title.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ArrayList<store>> entry = iterator.next();
            if (entry.getKey().contains(target)) {
                startTraversal = true;
            }
            if (startTraversal) {
                System.out.println("Key: " + entry.getKey());
                for (store s : entry.getValue()) {
                    count++;
                    s.printMessage();
                    if (count % 20 == 0) {
                        System.out.println("按Enter继续查询下一页（20个），按0退出查询");
                        scanner.nextLine();
                        input = scanner.nextLine();
                        if ("0".equals(input)) {
                            break;
                        }
                    }
                }
            }
        }
    }
    // 输出 registerList_name 表
    public void printRegisterListName() {
        int count = 0;
        Scanner scanner = new Scanner(System.in);
        for (Map.Entry<String, borrower> entry : registerList_name.entrySet()) {
            System.out.println(entry.getValue());
            count++;
            if (count % 20 == 0) {
                System.out.println("按Enter继续查询下一页（20个），按0退出查询");
                String input = scanner.nextLine();
                if ("0".equals(input)) {
                    break;
                }
            }
        }
    }
    // 查询 registerList_name 表
    public void queryRegisterListName() {
        int count = 0;
        Scanner scanner = new Scanner(System.in);
        System.out.println("输入查询对象姓名");
        String targetName=scanner.next();
        borrower tgt = registerList_name.get(targetName);
        if (tgt != null) {
            System.out.println(tgt);
        } else {
            System.out.println("精确查询未找到目标");
        }
        System.out.println("按Enter继续进行模糊查询，按0退出查询");
        scanner.nextLine();
        if ("0".equals(scanner.nextLine())) {
            return;
        }
        System.out.println("以下是模糊查询的结果");
        for (Map.Entry<String, borrower> entry : registerList_name.entrySet()) {
            String name=entry.getKey();
            if (name.contains(targetName)) {
                System.out.println(entry.getValue());
                count++;
                if (count % 20 == 0) {
                    System.out.println("按Enter继续查询下一页（20个），按0退出查询");
                    String input = scanner.nextLine();
                    if ("0".equals(input)) {
                        break;
                    }
                }
            }
        }
    }
    // 输出 registerList_recognization 表
    public void printRegisterListRecognization() {
        int count = 0;
        Scanner scanner = new Scanner(System.in);
        for (Map.Entry<String, borrower> entry : registerList_recognization.entrySet()) {
            System.out.println(entry.getValue());
            count++;
            if (count % 20 == 0) {
                System.out.println("按Enter继续查询下一页（20个），按0退出查询");
                String input = scanner.nextLine();
                if ("0".equals(input)) {
                    break;
                }
            }
        }
    }
    // 查询 registerList_recognization 表
    public void queryRegisterListRecognization() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("输入查询对象识别码");
        String targetName=scanner.next();
        borrower tgt = registerList_recognization.get(targetName);
        if (tgt != null) {
            System.out.println(tgt);
        } else {
            System.out.println("未找到目标");
        }
    }
}
