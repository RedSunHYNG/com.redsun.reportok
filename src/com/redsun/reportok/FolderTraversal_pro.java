package com.redsun.reportok;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.logging.FileHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;
import javax.swing.*;
/**
 * @auther redsun_gxy
 * @version Pro_0.1
 */
public class FolderTraversal_pro {

    public static int workmode = 1;

    public static char language = 'c';

    public static char main_report_mode = 'c';

    public static JFileChooser fileChooser = new JFileChooser();

    public static boolean dev = false;

    private static FileHandler fileHandler;

    public static void main(String[] args) {
        System.out.println("工具能力:");
        System.out.println("比对指纹，工具是否正常（比主工具版本高），查询主报告未通过的失败项.");
        System.out.println("工具注意事项：");
        System.out.println("无法查询未完整运行的模块里条例情况.");
        System.out.println("只针对自动化报告. ");
        System.out.println("CTS数据量较大，运行中请耐心等待，可尝试回车防止卡死。");
        System.out.println("模式切换(输入1或2)：");
        System.out.println(" 1 : 报告整测  2 : 单项测试");
        System.out.println("主报告查询模式切换(输入a或h)：");
        System.out.println(" a : 自动模式(此模式需要确保运行XTS工具设备所有时间是同步的)  h : 手动模式(只适用于单项测试)");
        System.out.println("语言切换(输入c或z)：");
        System.out.println(" c : 汉语  z : 英语");
        System.out.println("=========================");
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.println("-------------------------");
                System.out.println("输入Y继续检测报告。");
                String choose = sc.next();
                switch (choose) {
                    case "Y" -> {
                        SelectPath();
                    }
                    case "y" -> {
                        SelectPath();
                    }
                    case "C" -> {
                        language = 'c';
                    }
                    case "c" -> {
                        language = 'c';
                    }
                    case "E" -> {
                        language = 'e';
                    }
                    case "e" -> {
                        language = 'e';
                    }
                    case "A" -> {
                        main_report_mode = 'a';
                    }
                    case "a" -> {
                        main_report_mode = 'a';
                    }
                    case "H" -> {
                        main_report_mode = 'h';
                    }
                    case "h" -> {
                        main_report_mode = 'h';
                    }
                    case "dev" -> {
                        dev = !dev;
                    }
                    case "1" -> {
                        workmode = 1;
                    }
                    case "2" -> {
                        workmode = 2;
                    }
                    default -> {
                        if (fileHandler != null) {
                            fileHandler.close();
                        }
                        System.exit(0);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void SelectPath() throws IOException {
        if (workmode == 2 && main_report_mode == 'h') {
            System.out.println("选择单项内主报告的目录。");
        } else {
            System.out.println("选择总报告/单项的目录。");
        }
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fileChooser.showOpenDialog(null);
        String filePath = "";
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            filePath = fileChooser.getSelectedFile().getAbsolutePath();
        } else {
            System.out.println("未选择文件，检查已终止。");
            return;
        }

        if (filePath == null || filePath.isEmpty()) {
            System.out.println("路径无效，检查已终止。");
            return;
        }

        if (workmode == 1) {
            all_run(filePath);
        } else {
            unit_run(filePath);
        }
    }

    public static void all_run(String filePath) {
        System.out.println("当前模式-整测模式");
        System.out.println("此模式需要满足以下条件之一：");
        System.out.println("1. 单项内多个报告中的主报告命名路径上‘主报告’的标记");
        System.out.println("2. 确保运行XTS工具设备所有时间是同步的,并且单项主报告为最先创建的。");
        int lastIndex = filePath.lastIndexOf('\\');
        if (lastIndex == -1) {
            System.out.println("没有找到“\\”。");
            return;
        }
        Path rootPath = Paths.get(filePath);
        try (Stream<Path> stream = Files.list(rootPath)) {
            stream.filter(Files::isDirectory).forEach(path -> {
                run(path.toAbsolutePath().toString(), "");
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UncheckedIOException e) {
            System.out.println("没有此文件下的权限" + rootPath);
            e.printStackTrace();
        }
    }

    public static void unit_run(String filePath) {
        System.out.println("当前模式-单测模式");
        if (main_report_mode == 'a') {
            System.out.println("运行方式为:自动");
            System.out.println("此运行方式需要满足以下条件之一：");
            System.out.println("1. 单项内多个报告中的主报告命名路径上‘主报告’的标记");
            System.out.println("2. 确保运行XTS工具设备所有时间是同步的,并且单项主报告为最先创建的。");
        } else if (main_report_mode == 'h') {
            System.out.println("运行方式为:手动");
            System.out.println("此运行方式需要满足以下条件之一：");
            System.out.println("1. 请确保一开始选择的文件夹为此单项的主报告。");
            System.out.println("2. 请确保其他副报告放在与主报告的同文件内，或同文件夹内的文件夹内。");
        }
        // No Test
        String url = splitPath(filePath).getParentPath();
        run(url, filePath);
    }

    public static PathResult splitPath(String folderPath) {
        try {
            Path path = Paths.get(folderPath).normalize();
            Path lastFolder = path.getFileName();
            if (lastFolder == null) {
                return null; 
            }
            Path parentPath = path.getParent();
            return new PathResult(
                    lastFolder.toString(),
                    parentPath != null ? parentPath.toString() : "");
        } catch (Exception e) {
            return null; 
        }
    }

    public static class PathResult {
        private final String lastFolderName;
        private final String parentPath;

        public PathResult(String lastFolderName, String parentPath) {
            this.lastFolderName = lastFolderName;
            this.parentPath = parentPath;
        }

        public String getLastFolderName() {
            return lastFolderName;
        }

        public String getParentPath() {
            return parentPath;
        }
    }

    public static void run(String ReportUrl, String MainReport) {
        Map<String, Pro_unit> ReportListMap = new HashMap<>();
        Path rootPath = Paths.get(ReportUrl);
        System.out.println("遍历报告文件中。");
        Integer[] haveMainreport = {0};
        long beginTime = (long) 0;
        String beginTimeurl = "";
        try (Stream<Path> stream = Files.walk(rootPath)) {
            stream.filter(Files::isDirectory).forEach(path -> {
                String url = path.toAbsolutePath().toString();
                if (url.contains("\\test_result_failures_suite.html")) {
                    url = url.replace("\\test_result_failures_suite.html", "");
                    PathResult temp_Value = splitPath(url);
                    if (workmode == 1 || main_report_mode == 'a') {
                        if (url.contains("主报告")) {
                            haveMainreport[0] = 1;
                        }
                        /*
                         * 等待DEV:
                         * 方式一:获取文件名的时间进行对比
                         * 方式二:获取单项报告中的众多报告中的test_result_failures_suite的时间进行对比
                         * 将已经获得的最早时间赋值给beginTime,同时属于此时间的报告的路径赋值给beginTimeurl
                         * long reporttimes = 等待资料得到获取时间的方法，转换为秒进行比较大小。
                         * if(beginTime == (long) 0 || beginTime > reporttimes){
                         * beginTimes = reporttimes;
                         * beginTimeurl = temp_Value.getLastFolderName();
                         * }
                         */
                    } else if (url.equals(MainReport)) {
                        haveMainreport[0] = 1;
                    }
                    if (haveMainreport[0] == 1) {
                        Pro_unit report_ = new Pro_unit(temp_Value.getLastFolderName(), 0, temp_Value.getParentPath());
                        ReportListMap.put(temp_Value.getLastFolderName(), report_);
                        haveMainreport[0] = 2;
                    } else {
                        Pro_unit report_ = new Pro_unit(temp_Value.getLastFolderName(), 1, temp_Value.getParentPath());
                        ReportListMap.put(temp_Value.getLastFolderName(), report_);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UncheckedIOException e) {
            System.out.println("没有此文件下的权限" + rootPath);
            e.printStackTrace();
        }
        if (haveMainreport[0] == 0) {
            System.out.println("正在通过时间对比选取主报告。" + ReportUrl);
            Pro_unit report_ = ReportListMap.get(beginTimeurl);
            report_.ReviseType(0);
            // NOTEST 地址类型不用写回。
        }
        Pro_unit Pro_unit_Main_report = new Pro_unit();
        for (Pro_unit value : ReportListMap.values()) {
            String Pro_unit_report_Url = value.getUrl();
            int type_ = value.getType();
            Path root_Pro_unit_report_Url = Paths.get(Pro_unit_report_Url);
            try (Stream<Path> stream = Files.list(root_Pro_unit_report_Url)) {
                stream.filter(Files::isDirectory).forEach(path -> {
                    String son_Url = path.toAbsolutePath().toString();
                    if (type_ == 0 && !son_Url.contains("\\module_reports\\") && son_Url.contains("\\test_result_failures_suite.html")) {
                        try (BufferedReader br = new BufferedReader(new FileReader(son_Url))) {
                            String line;
                            String now_module = "";
                            String now_case = "";
                            int now_type = 0;
                            while ((line = br.readLine()) != null) {
                                if (dev) {
                                    System.out.println(line);
                                }
                                line = line.replace(" ", "").replace("&nbsp;", " ");
                                if (now_type == 0 && line.contains("<tdclass=\"rowtitle\">Suite/Build</td><td>")) {
                                    // NoTest
                                    line = line.replace("<tdclass=\"rowtitle\">Suite/Build</td><td>", "");
                                    line = line.replace("</td>", "");
                                    value.ReviseBulidTool(line);
                                } else if (now_type == 0&& line.contains("<tdclass=\"rowtitle\">Fingerprint</td><td>")) {
                                    // 获取指纹
                                    line = line.replace("<tdclass=\"rowtitle\">Fingerprint</td><td>", "");
                                    line = line.replace("</td>", "");
                                    value.ReviseFinger(line);
                                } else if (line.contains("<tdclass=\"module\"colspan=\"3\"><aname=\"")) {
                                    // 当前轮询到的模块名
                                    line = line.replace("<tdclass=\"module\"colspan=\"3\"><aname=\"", "");
                                    line = line.replace("</a></td>", "");
                                    // DEV - startIndex += startTag.length();
                                    int startIndex = line.indexOf("\">") + 2;
                                    if (startIndex == -1) {
                                        // 没有找到开始标记
                                        System.out.println("可能是无效条例？  " + line);
                                    }
                                    now_module = line.substring(startIndex);
                                }else if (line.contains("<tdclass=\"testname\">") && line.contains("</td><tdclass=\"failed\">")) {
                                    // 获取Fail的case
                                    line = line.replace("<tdclass=\"testname\">", "");
                                    now_case = line.replace("</td><tdclass=\"failed\">", "");
                                    // 组合模块与用例添加进相应的list
                                    List<String> NoPass_ = value.getNoPass();
                                    NoPass_.add(now_module + " " + now_case);
                                }else if(line.contains("IncompleteModules")){
                                    // 获取未完整运行的模块
                                    now_type = 1;
                                }else if (now_type  == 1 && line.contains("<td><aname=\"") && line.contains("</a></td>") ){
                                    line = line.replace("<td><aname=\"", "");
                                    line = line.replace("</a></td>", "");
                                    int startIndex = line.indexOf("\">") + 2;
                                    if (startIndex == -1) {
                                        System.out.println("错误汇入的case:"+line);
                                    }
                                    List<String> Module_ = value.getModule();
                                    Module_.add(line.substring(startIndex));
                                }
                            }
                        } catch (IOException e) {
                            System.out.println("读取文件内容失败。");
                            e.printStackTrace();
                        }
                    }else if (type_ == 1) {
                        if (son_Url.contains("\\test_result_failures_suite.html")){
                            try (BufferedReader br = new BufferedReader(new FileReader(son_Url))) {
                                String line;
                                Boolean BuildToolsIsOk = false;
                                Boolean FingerIsOk = false;
                                loop : while ((line = br.readLine()) != null) {
                                    if (dev) {
                                        System.out.println(line);
                                    }
                                    line = line.replace(" ", "").replace("&nbsp;", " ");
                                    if (line.contains("<tdclass=\"rowtitle\">Suite/Build</td><td>")) {
                                        // NoTest
                                        line = line.replace("<tdclass=\"rowtitle\">Suite/Build</td><td>", "");
                                        line = line.replace("</td>", "");
                                        value.ReviseBulidTool(line);
                                        BuildToolsIsOk = true;
                                    } else if (line.contains("<tdclass=\"rowtitle\">Fingerprint</td><td>")) {
                                        // 获取指纹
                                        line = line.replace("<tdclass=\"rowtitle\">Fingerprint</td><td>", "");
                                        line = line.replace("</td>", "");
                                        value.ReviseFinger(line);
                                        FingerIsOk = true;
                                    }
                                    if(BuildToolsIsOk && FingerIsOk){
                                        break loop;
                                    }
                                }
                            } catch (IOException e) {
                                System.out.println("读取文件内容失败。");
                                e.printStackTrace();
                            } catch (UncheckedIOException e) {
                                System.out.println("没有此文件下的权限" + son_Url);
                                e.printStackTrace();
                            }
                        } else if (son_Url.contains("\\module_reports\\") && son_Url.contains(".html")){
                             try (BufferedReader br = new BufferedReader(new FileReader(son_Url))) {
                                String line;
                                String now_module = "";
                                String now_case = "";
                                int now_type = 0;
                                while ((line = br.readLine()) != null) {
                                    if (dev) {
                                        System.out.println(line);
                                    }
                                    if (line.contains("<tdclass=\"module\"colspan=\"3\"><aname=\"")) {
                                    // 当前轮询到的模块名
                                        line = line.replace("<tdclass=\"module\"colspan=\"3\"><aname=\"", "");
                                        line = line.replace("</a></td>", "");
                                        int startIndex = line.indexOf("\">") + 2;
                                        if (startIndex == -1) {
                                            // 没有找到开始标记
                                            System.out.println("可能是无效条例？  " + line);
                                        }
                                        now_module = line.substring(startIndex);
                                    } else if (line.contains("<tdclass=\"testname\">") && line.contains("</td><tdclass=\"pass\">")){
                                        line = line.replace("<tdclass=\"testname\">", "");
                                        now_case = line.replace("</td><tdclass=\"pass\">", "");
                                        // 组合模块与用例添加进相应的list
                                        List<String> Pass_ = value.getPass();
                                        Pass_.add(now_module + " " + now_case);
                                    }else if (line.contains("<tdclass=\"testname\">") && line.contains("</td><tdclass=\"failed\">")) {
                                        // 获取Fail的case
                                        line = line.replace("<tdclass=\"testname\">", "");
                                        now_case = line.replace("</td><tdclass=\"failed\">", "");
                                        // 组合模块与用例添加进相应的list
                                        List<String> NoPass_ = value.getNoPass();
                                        NoPass_.add(now_module + " " + now_case);
                                    }else if(line.contains("IncompleteModules")){
                                        // 获取未完整运行的模块
                                        now_type = 1;
                                    }else if (now_type  == 1 && line.contains("<td><aname=\"") && line.contains("</a></td>") ){
                                        line = line.replace("<td><aname=\"", "");
                                        line = line.replace("</a></td>", "");
                                        int startIndex = line.indexOf("\">") + 2;
                                        if (startIndex == -1) {
                                            System.out.println("错误汇入的case:"+line);
                                        }
                                        List<String> Module_ = value.getModule();
                                        Module_.add(line.substring(startIndex));
                                    }
                                }
                            } catch (IOException e) {
                                System.out.println("读取文件内容失败。");
                                e.printStackTrace();
                            } catch (UncheckedIOException e) {
                                System.out.println("没有此文件下的权限" + son_Url);
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            } catch (UncheckedIOException e) {
                System.out.println("没有此文件下的权限" + rootPath);
                e.printStackTrace();
            }
            if (type_ == 0) {
                Pro_unit_Main_report = value;
            }
        }
        ArrayList<String> SayLoss_ = Pro_unit_Main_report.getNoPass();
        String MainTools = Pro_unit_Main_report.getBulidTool();
        int startIndex = MainTools.indexOf("/") + 1;
        String MainTools1 = MainTools.substring(0,startIndex);
        Long MainTools2 = Long.parseLong(MainTools.substring(startIndex));
        System.out.println("单项：" + ReportUrl + "\n");
        for (Pro_unit value : ReportListMap.values()) {
            int type_ = value.getType();
            if(type_ == 0){
                continue;
            }
            if(!value.getFinger().equals(Pro_unit_Main_report.getFinger())){
                System.out.println("----------------------");
                System.out.println("主报告指纹:");
                System.out.println(Pro_unit_Main_report.getFinger());
                System.out.println("与主报告指纹不一致的报告:");
                System.out.println("名字:" + value.getName());
                System.out.println("路径:" + value.getUrl());
                System.out.println("指纹:" + value.getFinger());
                System.out.println("----------------------");
            }
            String ViceTools = Pro_unit_Main_report.getBulidTool();
            int startIndex_ = ViceTools.indexOf("/") + 1;
            String ViceTools1 = ViceTools.substring(0,startIndex_);
            Long ViceTools2 = Long.parseLong(ViceTools.substring(startIndex_));
            if (!MainTools1.equals(ViceTools1) || MainTools2 > ViceTools2) {
                System.out.println("----------------------");
                System.out.println("主报告工具:");
                System.out.println(MainTools);
                System.out.println("不合理的构建工具的报告:");
                System.out.println("名字:" + value.getName());
                System.out.println("路径:" + value.getUrl());
                System.out.println("工具:" + ViceTools);
                System.out.println("----------------------");  
            }
            boolean MorefailshouldSay = true;
            ArrayList<String> ViceFail_ = value.getNoPass();
            for (String i : ViceFail_) {
                if (!SayLoss_.contains(i)) {
                    if(MorefailshouldSay){
                        System.out.println("----------------------");
                        System.out.println("副报告内额外多出的Fail条列:");
                        System.out.println("名字:" + value.getName());
                        System.out.println("路径:" + value.getUrl());
                        System.out.println("条列:");
                        MorefailshouldSay = false;
                    }
                    System.out.println(i);
                }
            }
            ArrayList<String> VicePass_ = value.getPass();
            for (String i : VicePass_) {
                if (SayLoss_.contains(i)) {
                    SayLoss_.remove(i);
                }
            }
        }
        System.out.println("----------------------");
        System.out.println("单例：" + ReportUrl);
        System.out.println("主报告遗漏的Fail条例:"+SayLoss_.size());
        for(String i : SayLoss_){
            System.out.println(i);
        }
    }
}
