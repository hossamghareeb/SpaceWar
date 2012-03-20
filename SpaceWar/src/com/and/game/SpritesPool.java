package com.and.game;

import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.util.pool.GenericPool;

public class SpritesPool extends GenericPool<Sprite>{

	private TextureRegion spriteTextureRegion;
	public SpritesPool(TextureRegion textureRegion)
	{
		if(textureRegion == null)
		{
		  throw new IllegalArgumentException("not accepted texture");
		  
		}
		spriteTextureRegion = textureRegion;
	}
	@Override
	protected Sprite onAllocatePoolItem() {
	
		return new Sprite(andActivity.currentSpriteX, andActivity.currentSpriteY
				, spriteTextureRegion.deepCopy());
	}
	
	/** Called when a Sprite is sent to the pool */
	    protected void onHandleRecycleItem(final Sprite sprite) {
	    	sprite.clearEntityModifiers();
	    	sprite.clearUpdateHandlers();
	        sprite.setVisible(false);
	        sprite.detachSelf();
	        sprite.reset();
	
	    }

}
