package com.jawnnypoo.zed.graphics;


/**
 * Simple container class for textures.  Serves as a mapping between Android resource ids and
 * OpenGL texture names, and also as a placeholder object for textures that may or may not have
 * been loaded into vram.  Objects can cache Texture objects but should *never* cache the texture
 * name itself, as it may change at any time.
 */
public class Texture {
    public int resource;
    public int name;
    public int width;
    public int height;
    public boolean loaded;
    
    public Texture() {
        reset();
    }
    
    public void reset() {
        resource = -1;
        name = -1;
        width = 0;
        height = 0;
        loaded = false;
    }
}