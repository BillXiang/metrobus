package com.bill.metrobus.bluetoothchat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;

abstract class MyProgressDialog
{
	public static void indeterminate(Context context, Handler handler, String message, final Runnable runnable, OnDismissListener dismissListener,
		boolean cancelable)
	{

		try
		{

			indeterminateInternal(context, handler, message, runnable, dismissListener, cancelable, null);
		}
		catch (Exception e)
		{

			; // nop.
		}
	}

	public static void indeterminate(Context context, Handler handler, String message, final Runnable runnable, OnDismissListener dismissListener,
			boolean cancelable, OnCancelListener onCancelListener)
		{

			try
			{

				indeterminateInternal(context, handler, message, runnable, dismissListener, cancelable, onCancelListener );
			}
			catch (Exception e)
			{

				; // nop.
			}
		}
	
	private static ProgressDialog createProgressDialog(Context context, String message)
	{

		ProgressDialog dialog = new ProgressDialog(context);
		dialog.setIndeterminate(false);
		dialog.setMessage(message);

		return dialog;
	}


	private static void indeterminateInternal(Context context, 
											  final Handler handler, 
											  String message, 
											  final Runnable runnable, 
											  OnDismissListener dismissListener, 
											  boolean cancelable,
											  OnCancelListener onCancelListener)
	{
		final ProgressDialog dialog = createProgressDialog(context, message);
		dialog.setCancelable(cancelable);

		if (dismissListener != null)
			dialog.setOnDismissListener(dismissListener);
		if(onCancelListener!=null)
			dialog.setOnCancelListener(onCancelListener);
		dialog.show();

		new Thread() 
		{
			@Override
			public void run()
			{
				runnable.run();
				handler.post(new Runnable() 
				{
					public void run()
					{
						try
						{
							dialog.dismiss();
						}
						catch (Exception e)
						{
							; // nop.
						}

					}
				});
			};
		}.start();
	}
}
