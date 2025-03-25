import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Stream;
import javax.swing.*;

    /*
     * @auther redsun_gxy
     * @version .3
     */
public class FolderTraversal {
    // 主报告名
    public static String main_report_name ="";
    // 主报告fail项目
    public static ArrayList<String> main_report = new ArrayList<>();
    // 主报告未运行的模块
    public static ArrayList<String> main_report_tell = new ArrayList<>();
    // 主报告未运行的模块
    public static ArrayList<String> main_report_module = new ArrayList<>();
    // 副报告pass项目
    public static ArrayList<String> other_report_pass = new ArrayList<>();
    // 主报告外额外的fail项目
    public static ArrayList<String> other_report_fail = new ArrayList<>();
    public static ArrayList<String> other_report_fail_tell = new ArrayList<>();
    public static String DateTime = LocalDateTime.now().toString();
    public static String main_report_finger ="";
    public static long main_report_bulid;
    public static JFileChooser fileChooser = new JFileChooser();

    public static boolean dev = false;
    
    private static final Logger logger = Logger.getLogger(FolderTraversal.class.getName());

    private static FileHandler fileHandler;

    private static final String LOG_FILE_NAME = DateTime.replaceAll("[:/]", "-") + ".log";

    static {
        for (var handler : logger.getHandlers()) {
            logger.removeHandler(handler);
        }
        try {
            fileHandler = new FileHandler( "./" + LOG_FILE_NAME, true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
            logger.setUseParentHandlers(false); 
        } catch (SecurityException | IOException e) {
            throw new RuntimeException("Error setting up logger", e);
        }
    }
    public static void main(String[] args){
        System.out.println("本工具仅用于XTS系列中报告的审查。");
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.println("-----------------------------------");
                System.out.println("输入Y继续检测报告。");
                String choose = sc.next();
                if(choose.equals("Y") || choose.equals("y")){
                    main_report_name = "";
                    main_report.clear();
                    main_report_tell.clear();
                    main_report_module.clear();
                    other_report_pass.clear();
                    other_report_fail.clear();
                    other_report_fail_tell.clear();
                    DateTime = LocalDateTime.now().toString();
                    System.out.println("已准备就绪。");
                    try {
                        run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(choose.equals("dev")){
                    dev = !dev;
                }else{
                    if (fileHandler != null) {
                        fileHandler.close();
                    }
                    System.exit(0);
                }
            }

        }
    }
    
    public static void run() throws IOException {
        System.out.println("选择主报告的目录。");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fileChooser.showOpenDialog(null);
        String filePath = "";
        if(returnVal == JFileChooser.APPROVE_OPTION){       
            filePath= fileChooser.getSelectedFile().getAbsolutePath();
        }else{
            System.out.println("未选择文件，检查已终止。");
            return;
        }
        
        if (filePath == null || filePath.isEmpty())  {
            System.out.println("路径无效，检查已终止。");
            return;
        }
        int lastIndex = filePath.lastIndexOf('\\');
        String report_url = "";
        if (lastIndex == -1) {
            // 无效链接的处理
            System.out.println("没有找到“\\”。");
            return;
        } else {
            // 获取主报告名字
            report_url = filePath.substring(0, lastIndex);
            System.out.println(report_url);
            main_report_name = filePath.substring(lastIndex + 1);
            System.out.println("主报告名字：" + main_report_name);
        }
        ArrayList<String> all_url = new ArrayList<>();
        Path rootPath = Paths.get(report_url);
        System.out.println("遍历报告文件中。");
        try (Stream<Path> stream = Files.walk(rootPath)) {
            stream.filter(Files::isRegularFile)
                  .forEach(path -> all_url.add(path.toAbsolutePath().toString()));;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UncheckedIOException e){
            System.out.println("没有此文件下的权限"+rootPath);
            e.printStackTrace();
        }
        for(int n = 0 ; n  < all_url.size(); n++){
            String url = all_url.get(n);
            // DEV-System.out.println(url);
            if(url.contains(main_report_name+"\\test_result_failures_suite.html")) {
                if (dev) {
                    System.out.println(url);
                }
                //遍历此文件每行
                try (BufferedReader br = new BufferedReader(new FileReader(url))) {
                    String line;
                    String now_module = "";  
                    String now_case = "";
                    int now_type = 0;
                    while ((line = br.readLine()) != null) {
                        if (dev){
                            System.out.println(line);
                        }
                        // 处理每一行内容
                        String add = line.replace(" ", "");
                        add = add.replace("&nbsp;", " ");
                        if (now_type == 0 && add.contains("<tdclass=\"rowtitle\">Suite/Build</td><td>")){
                            add = add.replace("<tdclass=\"rowtitle\">Suite/Build</td><td>", "");
                            add = add.replace("</td>", "");
                            int startIndex = add.indexOf("/") + 1;
                            System.out.println("主报告构建工具:" + add);
                            main_report_bulid =  Long.parseLong(add.substring(startIndex));
                        }
                        if (now_type == 0 && add.contains("<tdclass=\"rowtitle\">Fingerprint</td><td>")){
                            add = add.replace("<tdclass=\"rowtitle\">Fingerprint</td><td>", "");
                            add = add.replace("</td>", "");
                            main_report_finger = add;
                            System.out.println("主报告指纹:" + main_report_finger);
                        }
                        if (now_type == 0 && add.contains("<tdclass=\"module\"colspan=\"3\"><aname=\"")){
                            add = add.replace("<tdclass=\"module\"colspan=\"3\"><aname=\"", "");
                            add = add.replace("</a></td>", "");
                            //startIndex += startTag.length();
                            int startIndex = add.indexOf("\">") + 2;
                            if (startIndex == -1) {
                                System.out.println("错误汇入的case:"+add);
                            }
                            now_module = add.substring(startIndex);
                        }
                        //failed 
                        if (now_type == 0 && add.contains("<tdclass=\"testname\">") && add.contains("</td><tdclass=\"failed\">")){
                            add = add.replace("<tdclass=\"testname\">", "");
                            now_case = add.replace("</td><tdclass=\"failed\">", "");
                            // 组合模块与用例添加进相应的list
                            main_report.add(now_module+" "+now_case);
                        }
                        if(add.contains("IncompleteModules")){
                            now_type = 1;
                        }
                        if (now_type  == 1 && add.contains("<td><aname=\"") && add.contains("</a></td>") ){
                            add = add.replace("<td><aname=\"", "");
                            add = add.replace("</a></td>", "");
                            int startIndex = add.indexOf("\">") + 2;
                            if (startIndex == -1) {
                                System.out.println("错误汇入的case:"+add);
                            }
                            main_report_module.add(add.substring(startIndex));
                        }
                    }
                } catch (IOException e) {
                    System.out.println("读取文件内容失败。");
                    e.printStackTrace();
                }
            }
            if(!url.contains("\\"+main_report_name+"\\") && url.contains("\\test_result_failures_suite.html")) {
                try (BufferedReader br = new BufferedReader(new FileReader(url))) {
                    String line;
                    Boolean build_isok = false;
                    Boolean finger_isok = false;
                    loop:
                    while ((line = br.readLine()) != null) {
                        String add = line.replace(" ", "");
                        add = add.replace("&nbsp;", " ");
                        if (add.contains("<tdclass=\"rowtitle\">Suite/Build</td><td>")) {
                            add = add.replace("<tdclass=\"rowtitle\">Suite/Build</td><td>", "");
                            add = add.replace("</td>", "");
                            int startIndex = add.indexOf("/") + 1;
                            long temp_main_report_bulid = Long.parseLong(add.substring(startIndex));
                            if (main_report_bulid > temp_main_report_bulid) {
                                System.out.println("副报告工具疑似存在问题:");
                                System.out.println("工具:" + add);
                                System.out.println("地址:" + url);
                            }
                            build_isok = true;
                        }
                        if (add.contains("<tdclass=\"rowtitle\">Fingerprint</td><td>")) {
                            add = add.replace("<tdclass=\"rowtitle\">Fingerprint</td><td>", "");
                            add = add.replace("</td>", "");

                            if (!add.equals(main_report_finger)) {
                                System.out.println("副报告指纹疑似存在问题:");
                                System.out.println("指纹:" + add);
                                System.out.println("地址:" + url);
                            }
                            finger_isok = true;
                        }
                        if (build_isok && finger_isok) {
                            break loop;
                        }
                    }
                } catch (IOException e) {
                    System.out.println("读取文件内容失败。");
                    e.printStackTrace();
                }
            }
            if(!url.contains("\\"+main_report_name+"\\") && url.contains("\\module_reports\\") && url.contains(".html")){
                if (dev) {
                    System.out.println(url);
                }
                //遍历此文件每行
                try (BufferedReader br = new BufferedReader(new FileReader(url))) {
                    String line;
                    String now_module = "";  
                    String now_case = "";
                    while ((line = br.readLine()) != null) {
                        // 处理每一行内容
                        String add = line.replace(" ", "");
                        add = add.replace("&nbsp;", " ");
                        //模块
                        if (add.contains("<tdclass=\"module\"colspan=\"3\"><aname=\"")){
                            add = add.replace("<tdclass=\"module\"colspan=\"3\"><aname=\"", "");
                            add = add.replace("</a></td>", "");
                            //DEV - startIndex += startTag.length();
                            int startIndex = add.indexOf("\">") + 2;
                            if (startIndex == -1) {
                                // 没有找到开始标记
                                System.out.println("可能是无效条例？  "+add);
                            }
                            now_module = add.substring(startIndex);
                        }
                        //pass （主报告无此）
                        if (add.contains("<tdclass=\"testname\">") && add.contains("</td><tdclass=\"pass\">")){
                            add = add.replace("<tdclass=\"testname\">", "");
                            now_case = add.replace("</td><tdclass=\"pass\">", "");
                            // 组合模块与用例添加进相应的list
                            other_report_pass.add(now_module+" "+now_case);
                        }
                        //failed 
                        if (add.contains("<tdclass=\"testname\">") && add.contains("</td><tdclass=\"failed\">")){
                            add = add.replace("<tdclass=\"testname\">", "");
                            now_case = add.replace("</td><tdclass=\"failed\">", "");
                            // 组合模块与用例添加进相应的list
                            other_report_fail.add(now_module+" "+now_case);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("读取文件内容失败。");
                    e.printStackTrace();
                }
            }    
        }
        // 比较并输出
        for (String item : other_report_fail) {
            if(!main_report.contains(item)){
                other_report_fail_tell.add(item);
            }
        }
        for (String item : main_report) {
            if(!other_report_pass.contains(item)){
                main_report_tell.add(item);
            }
        }
        logger.log(Level.INFO, "副报告多出的失败条列:");
        System.out.println("副报告多出的失败条列:"+other_report_fail_tell.size());
        for (String item : other_report_fail_tell) {
            logger.log(Level.INFO, item);
            System.out.println(item);
        }
        System.out.println("  ");
        logger.log(Level.INFO, "无法确定未完整运行的模块是否完全通过。");
        System.out.println("无法确定未完整运行的模块是否完全通过。");
        logger.log(Level.INFO, "需要手动查看");
        System.out.println("需要手动查看");
        logger.log(Level.INFO, "主报告未完整运行的模块条例:");
        System.out.println("主报告未完整运行的模块条例:"+main_report_module.size());
        for (String item : main_report_module) {
            logger.log(Level.INFO, item);
            System.out.println(item);
        }
        System.out.println("  ");
        logger.log(Level.INFO, "主报告遗漏的失败条例:");
        System.out.println("主报告遗漏的失败条例:"+main_report_tell.size());
        for (String item : main_report_tell) {
            logger.log(Level.INFO, item);
            System.out.println(item);
        }
        System.out.println("  ");
    }
}
