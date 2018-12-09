package com.inoxapps.funrun.utils;


public interface ActionResolver {

	public interface PopupCallback {
		public void onClicked(boolean isYesClicked);
	}

	public interface InputDialogCallback {
		void onInputRecieved(boolean b, String value);
	}

	void gameFinish();

	boolean isNetworkAvailable();

	void showPopup(String title, String message, String positiveButton, String negativeButton, boolean isCancellable,
			PopupCallback callback);

	void getInputFromUser(String title, String message, InputDialogCallback inputDialogCallback);
}
