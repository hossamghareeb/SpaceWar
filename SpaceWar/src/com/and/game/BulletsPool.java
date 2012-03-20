package com.and.game;

import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.util.pool.GenericPool;

public class BulletsPool extends GenericPool<Sprite>{

	private TextureRegion spriteTextureRegion;
	public BulletsPool(TextureRegion textureRegion)
	{
		if(textureRegion == null)
		{
		  throw new IllegalArgumentException("not accepted texture");
		  
		}
		spriteTextureRegion = textureRegion;
	}
	@Override
	protected Sprite onAllocatePoolItem() {
	
		return null;
	}

}
