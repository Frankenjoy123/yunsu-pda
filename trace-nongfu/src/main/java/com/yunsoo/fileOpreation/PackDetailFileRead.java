package com.yunsoo.fileOpreation;

import android.os.Environment;
import android.text.format.Time;

import com.yunsoo.unity.PackageDetail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PackDetailFileRead {
	private static String FOLDERNAME="/yunsoo";
	private  String prefix;
	private String lineString;
    private String fixedLineString;
	private List<String> keysList=new ArrayList<String>();
	
	private int i_year;
	private String mouthString;
	private String dayString;
    private String yesterdayString;
    private File fixedFile;

	private List<PackageDetail> packageDetailList;
    private String fixedPreString;

    public String getFixPreString() {
        return fixedPreString;
    }

    public File getFixedFile() {
        return fixedFile;
    }

    public PackDetailFileRead(String prefix) {
		super();
		this.prefix = prefix;
	}

    public String getFixedLineString() {
        return fixedLineString;
    }

    public List<String> getProductsByPackCode(String packCode){
        List<String> resultList=new ArrayList<String>();
        File parentFile=new File(Environment.getExternalStorageDirectory()+FOLDERNAME);
        File[] childrenFiles= parentFile.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {

                if(pathname.getName().startsWith(prefix)){
                    return true;
                }
                return false;
            }
        });

        for(int i=0;i<childrenFiles.length;i++){
            File childFile=childrenFiles[i];
            try {

                BufferedReader br=new BufferedReader(new FileReader(childFile));

                while((lineString=br.readLine())!=null){



                    String[] arrayStrings=lineString.split(",");

                    if(arrayStrings[1].equals(packCode)){
                        fixedFile=childFile;
                        fixedPreString=arrayStrings[0]+","+arrayStrings[1];
                        fixedLineString=lineString;

                        for(int j=2;j<arrayStrings.length;j++){
                            resultList.add(arrayStrings[j]);
                        }
//                        return resultList;
                    }


                }

            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
        return  resultList;

    }



	public  List<PackageDetail> getPackageDetailList(Time nowTime) {
		
		packageDetailList=new ArrayList<PackageDetail>();
		
        i_year = nowTime.year;
        int i_month = nowTime.month+1;
        mouthString=(i_month<10)?("0"+i_month):(i_month+"");
        int i_day = nowTime.monthDay;

        int yesterday=i_day-1;
        yesterdayString=(yesterday<10)?("0"+yesterday):(yesterday+"");
        dayString=(i_day<10)?("0"+i_day):(i_day+"");
        File parentFile=new File(Environment.getExternalStorageDirectory()+FOLDERNAME);
        File[] childrenFiles= parentFile.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {

				if(pathname.getName().startsWith(prefix+i_year+"_"+mouthString+"_"+dayString)

                        ||pathname.getName().startsWith(prefix+i_year+"_"+mouthString+"_"+yesterdayString)){
					return true;
				}
				return false;
			}
		});
        for(int i=0;i<childrenFiles.length;i++){
        	File childFile=childrenFiles[i];
        	try {

				BufferedReader br=new BufferedReader(new FileReader(childFile));
				
				while((lineString=br.readLine())!=null){
					
					PackageDetail packageDetail=new PackageDetail();
					
					String[] arrayStrings=lineString.split(",");
                    if (arrayStrings!=null&&arrayStrings.length>2){
                        packageDetail.setPackageId(arrayStrings[1]);

                        List<String> products=new ArrayList<String>();

                        for(int j=2;j<arrayStrings.length;j++){
                            products.add(arrayStrings[j]);
                        }
                        packageDetail.setProductIdList(products);

                        packageDetailList.add(packageDetail);
                    }

				}

			} catch (FileNotFoundException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
        }
        return packageDetailList;
        
        
	}
	
}
