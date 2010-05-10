package cn.shooter.client.andriod;


import android.os.AsyncTask;

public class CommentAddTask extends AsyncTask<String, Void, String> {
	private SubDetailsActivity mActivity;
	private String mComm;
	private String mSubid;
	private String mThreadid;
	private String mMobileUser;
	
    public CommentAddTask(SubDetailsActivity activity, String comm, String subid, String threadid, String mobileUser) {
        mActivity = activity;
        mComm = comm;
        mSubid = subid;
        mThreadid = threadid;
        mMobileUser = mobileUser;
    }

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		mActivity.postComm( mComm, mSubid, mThreadid, mMobileUser);
		mActivity.handler.sendEmptyMessage(1);
		return null;
	}

}
