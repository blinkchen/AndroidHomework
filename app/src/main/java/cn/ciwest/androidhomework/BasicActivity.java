package cn.ciwest.androidhomework;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Administrator
 *
 */
public class BasicActivity extends Activity {

	private File dir;
	protected String photo_path;
	protected File photoFile;

    /**
     *方便所有的activity获取文件权限
     */
	public void securing(){
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

	/**
	 *方便所有的activity调用 AlertDialog
	 */
	public void show() {
		// TODO Auto-generated method stub
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				BasicActivity.this);
		builder.setTitle("选择照片");
		builder.setPositiveButton("相机", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface

										dialog, int which) {
				takeFromCamera();
			}
		});
		builder.setNegativeButton("相册", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface

										dialog, int which) {
				// 跳转到相册
				Intent intent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, 1);

			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * 相机获得照片
	 */
	private void takeFromCamera() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            dir = new File(Environment.getExternalStorageDirectory() + "/AndroidHomework/Camera");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            photo_path = dir + "/"
                    + new SimpleDateFormat(" ").format(new Date())
                    + ".jpg";
            photoFile = new File(photo_path);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            startActivityForResult(intent, 0);
        } else {
            Toast.makeText(this, "SD卡不存在,无法启动相机", Toast.LENGTH_SHORT).show();
        }
	}
}
