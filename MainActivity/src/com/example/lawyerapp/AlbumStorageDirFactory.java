package com.example.lawyerapp;

import java.io.File;

// This is used by PhotoIntentActivity 

abstract class AlbumStorageDirFactory {
	public abstract File getAlbumStorageDir(String albumName);
}
