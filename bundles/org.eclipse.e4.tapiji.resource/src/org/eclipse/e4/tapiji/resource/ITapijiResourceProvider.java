package org.eclipse.e4.tapiji.resource;


import org.eclipse.swt.graphics.Image;


public interface ITapijiResourceProvider {

    public Image loadImage(String path);

    public void dispose();
}
