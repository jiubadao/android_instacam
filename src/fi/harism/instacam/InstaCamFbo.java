/*
   Copyright 2012 Harri Smatt

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package fi.harism.instacam;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

/**
 * Helper class for handling frame buffer objects.
 */
public final class InstaCamFbo {

	// FBO handle.
	private int mFrameBufferHandle = -1;
	// Generated texture handles.
	private int[] mTextureHandles = {};
	// FBO textures and depth buffer size.
	private int mWidth, mHeight;

	/**
	 * Binds this FBO into use and adjusts viewport to FBO size.
	 */
	public void bind() {
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBufferHandle);
		GLES20.glViewport(0, 0, mWidth, mHeight);
	}

	/**
	 * Bind certain texture into target texture. This method should be called
	 * only after call to bind().
	 * 
	 * @param index
	 *            Index of texture to bind.
	 */
	public void bindTexture(int index) {
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,
				GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D,
				mTextureHandles[index], 0);
	}

	/**
	 * Getter for FBO height.
	 * 
	 * @return FBO height in pixels.
	 */
	public int getHeight() {
		return mHeight;
	}

	/**
	 * Getter for texture ids.
	 * 
	 * @param index
	 *            Index of texture.
	 * @return Texture id.
	 */
	public int getTexture(int index) {
		return mTextureHandles[index];
	}

	/**
	 * Getter for FBO width.
	 * 
	 * @return FBO width in pixels.
	 */
	public int getWidth() {
		return mWidth;
	}

	/**
	 * Initializes FBO with given parameters. Width and height are used to
	 * generate textures out of which all are sized same to this FBO. If you
	 * give genRenderBuffer a value 'true', depth buffer will be generated also.
	 * 
	 * @param width
	 *            FBO width in pixels
	 * @param height
	 *            FBO height in pixels
	 * @param textureCount
	 *            Number of textures to generate
	 * @param genDepthBuffer
	 *            If true, depth buffer is allocated for this FBO @ param
	 *            genStencilBuffer If true, stencil buffer is allocated for this
	 *            FBO
	 */
	public void init(int width, int height, int textureCount,
			boolean textureExternalOES) {

		// Just in case.
		reset();

		// Store FBO size.
		mWidth = width;
		mHeight = height;

		// Genereta FBO.
		int handle[] = { 0 };
		GLES20.glGenFramebuffers(1, handle, 0);
		mFrameBufferHandle = handle[0];
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBufferHandle);

		// Generate textures.
		mTextureHandles = new int[textureCount];
		GLES20.glGenTextures(textureCount, mTextureHandles, 0);
		int target = textureExternalOES ? GLES11Ext.GL_TEXTURE_EXTERNAL_OES
				: GLES20.GL_TEXTURE_2D;
		for (int texture : mTextureHandles) {
			GLES20.glBindTexture(target, texture);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_WRAP_T,
					GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_MIN_FILTER,
					GLES20.GL_NEAREST);
			GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_MAG_FILTER,
					GLES20.GL_LINEAR);
			if (target == GLES20.GL_TEXTURE_2D) {
				GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
						mWidth, mHeight, 0, GLES20.GL_RGBA,
						GLES20.GL_UNSIGNED_BYTE, null);
			}
		}
	}

	/**
	 * Resets this FBO into its initial state, releasing all resources that were
	 * allocated during a call to init.
	 */
	public void reset() {
		int[] handle = { mFrameBufferHandle };
		GLES20.glDeleteFramebuffers(1, handle, 0);
		GLES20.glDeleteTextures(mTextureHandles.length, mTextureHandles, 0);
		mFrameBufferHandle = -1;
		mTextureHandles = new int[0];
		mWidth = mHeight = 0;
	}

}
