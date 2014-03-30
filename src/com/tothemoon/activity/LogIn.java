package com.tothemoon.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.example.coursephototidy.R;
import com.tothemoon.data.CourseInformation;
import com.tothemoon.util.RangeCourse;
import com.tothemoon.util.StringDealer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class LogIn extends Activity{
	//��֤��
	Bitmap bitmap;
	ImageView showAuthCode;
	
	//��Ҫ���ݵ���Ϣ
	final String lessonName = new String("lessonName = ");
	final String teacherName = new String("teacherName = ") ;
	final String detail = new String("detail=");
	final String day = new String("day = ");
	final String beginTime = new String("beginTime = ");
	final String endTime = new String("endTime = ");
	String[] courses;
	String[] teachers;
	String[] details;
	String[] days;
	String[] beginTimes;
	String[] endTimes;
	
	HttpClient httpClient;
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){
		public void handleMessage(Message msg){
			if(msg.what == 0x123){
				try{
					showAuthCode.setImageBitmap(bitmap);
					Toast.makeText(LogIn.this,"�ѻ�ȡ��֤��",Toast.LENGTH_SHORT).show();
				}
				catch(Exception e){
					e.printStackTrace();
					Toast.makeText(LogIn.this,"�벻ҪƵ����ȡ��֤��",Toast.LENGTH_SHORT).show();
				}
			}
			if(msg.what == 0x234){
				Toast.makeText(LogIn.this,"��ȡ�α�ɹ�",Toast.LENGTH_SHORT).show();
			}
			if(msg.what == 0x345){
				Toast.makeText(LogIn.this,"��¼ʧ��,���������Ƿ���ȷ",Toast.LENGTH_SHORT).show();
			}
			if(msg.what == 0x456){
				Toast.makeText(LogIn.this,"����ʧ��,��������",Toast.LENGTH_SHORT).show();
			}
			if(msg.what == 0x567){
				Toast.makeText(LogIn.this,"�벻ҪƵ�����",Toast.LENGTH_SHORT).show();
			}
		}
	};
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		// ����DefaultHttpClient����
		httpClient = new DefaultHttpClient();
		showAuthCode = (ImageView) findViewById(R.id.show);	
	}
	public void getAuthCode(View v)
	{
		new Thread()
		{
			@Override
			public void run()
			{
				// ����һ��HttpGet����
				HttpGet get = new HttpGet("http://210.42.121.241/");
				//�������ѡ��ϵͳ
				try
				{
					// ����GET����
					httpClient.execute(get);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					Message msg = new Message();
					msg.what = 0x456;
					handler.sendMessage(msg);
				}
				//��ȡ��֤��
				get = new HttpGet("http://210.42.121.241/servlet/GenImg");
				try
				{
					HttpResponse httpResponse = httpClient.execute(get);
					HttpEntity entity = httpResponse.getEntity();
					if (entity != null)
					{
						try
						{
							// ��ȡ��������Ӧ
							InputStream is = entity.getContent();
							bitmap = BitmapFactory.decodeStream(is);
							handler.sendEmptyMessage(0x123);
							is.close();
						}
						catch(Exception e)
						{
							e.printStackTrace();
							Message msg = new Message();
							msg.what = 0x567;
							handler.sendMessage(msg);
						}
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
					Message msg = new Message();
					msg.what = 0x456;
					handler.sendMessage(msg);
				}
			}
		}.start();
	}
	public void showLogin(View v)
	{
		final String name = ((EditText) findViewById(R.id.name)).getText().toString();
		final String pass = ((EditText) findViewById(R.id.pass)).getText().toString();
		final String auth = ((EditText) findViewById(R.id.auth)).getText().toString();
		
		new Thread()
		{
			@Override
			public void run()
			{
				HttpPost post = new HttpPost("http://210.42.121.241/servlet/Login");
				try
				{							
					
					// ������ݲ��������Ƚ϶�Ļ����ԶԴ��ݵĲ������з�װ
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("id", name));
					params.add(new BasicNameValuePair("pwd", pass));
					params.add(new BasicNameValuePair("xdvfb",auth));
					
					// �����������
					post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
					httpClient.execute(post);
					
				}
				catch (Exception e)
				{
					e.printStackTrace();
					Message msg = new Message();
					msg.what = 0x456;
					handler.sendMessage(msg);
				}
				try
				{
					HttpGet get = new HttpGet("http://210.42.121.241/servlet/Svlt_QueryStuLsn?action=queryStuLsn");
					HttpResponse httpResponse = httpClient.execute(get);
					HttpEntity entity = httpResponse.getEntity();
					if (entity != null)
					{	
						
						// ��ȡ��������Ӧ
						String html=EntityUtils.toString(entity);
						
						//����html
						List<String> list;
						StringDealer sd=new StringDealer(html, lessonName);
						list=sd.praseString();
						courses = list.toArray(new String[list.size()]);
						
						//�ж��Ƿ��½�ɹ�
						if(courses.length==0)
						{
							Message msg = new Message();
							msg.what = 0x345;
							handler.sendMessage(msg);
						}
						else
						{	
							sd=new StringDealer(html, teacherName);
							list=sd.praseString();
							teachers = list.toArray(new String[list.size()]);
							
							sd=new StringDealer(html, detail);
							list=sd.praseString();
							details = list.toArray(new String[list.size()]);
							
							sd=new StringDealer(html, beginTime);
							list=sd.praseString();
							beginTimes = list.toArray(new String[list.size()]);
							
							sd=new StringDealer(html, endTime);
							list=sd.praseString();
							endTimes = list.toArray(new String[list.size()]);
							
							sd=new StringDealer(html,day);
							list=sd.praseString();
							list.remove(0);
							days = list.toArray(new String[list.size()]);
							
							RangeCourse.order(days, beginTimes,endTimes, courses, teachers, details);
							
							List<CourseInformation> listCI = new ArrayList<CourseInformation>();
							for(int i = 0;i<courses.length;i++){
								listCI.add(new CourseInformation(days[i],beginTimes[i],endTimes[i],courses[i],teachers[i],details[i]));
							}
							
							// ����һ��Intent
							Intent intent = new Intent(LogIn.this,
									Main.class);
							intent.putParcelableArrayListExtra("courseInformation",(ArrayList<? extends Parcelable>) listCI);
							// ����intent��Ӧ��Activity
							startActivity(intent);
							
							Message msg = new Message();
							msg.what = 0x234;
							handler.sendMessage(msg);
						}
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
					Message msg = new Message();
					msg.what = 0x456;
					handler.sendMessage(msg);
				}
			}
		}.start();
	}
}

