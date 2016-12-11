package com.tactileshow.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DataReader
{
    private final static String fileName = "/storage/sdcard0/TactileShow/BLE/mapdata1.txt";
    
    private static List<String> dataList;
    
    public static List<String> getDataList()
    {
        dataList = new ArrayList<String>();
        try
        {
            Scanner scanner = new Scanner(new File(fileName));
            while (scanner.hasNextLine())
            {
                dataList.add(scanner.nextLine());
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return dataList;
    }
}
