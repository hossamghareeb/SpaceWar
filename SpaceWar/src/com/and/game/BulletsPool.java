package com.and.game;

import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.util.pool.GenericPool;

public class BulletsPool extends GenericPool<Sprite>{

	private TextureRegion bulletTextureRegion;
	
	public BulletsPool(TextureRegion textureRegion)
	{
		if(textureRegion == null)
		{
		  throw new IllegalArgumentException("not accepted texture");
		  
		}
		bulletTextureRegion = textureRegion;
	}
	@Override
	protected Sprite onAllocatePoolItem() {
	
		return new Sprite(andActivity.currentBulletX, andActivity.currentBulletY
				, bulletTextureRegion.deepCopy());
	}
	
	/** Called when a Sprite is sent to the pool */
	    protected void onHandleRecycleItem(final Sprite bullet) {
	    	bullet.clearEntityModifiers();
	    	bullet.clearUpdateHandlers();
	    	bullet.setVisible(false);
	    	bullet.detachSelf();
	    	bullet.reset();
	
	    }

}
