package lvl2advanced.p01gui.p01simple;


import lwjglutils.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL30;
import transforms.Camera;
import transforms.Mat4PerspRH;
import transforms.Vec3D;

import java.io.IOException;
import java.io.InputStream;
import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL20C.*;


/**
* 
* @author PGRF FIM UHK
* @version 2.0
* @since 2019-09-02
*/
public class Renderer extends AbstractRenderer{
    private OGLBuffers buffers;
    private int locView,locPosX,locPosZ,locPosY,locProjection, locTime, locViewLight,shaderProgramLight, shaderProgramViewer,locProjectionLight,locTimeLight,locTypeLight,locLightVP, locTeleso, locTelesoLight;
    double ox, oy;
    boolean mouseButton1 = false;
    private Camera cam, camLight;
    private Mat4PerspRH projection;
    private float time;
    private OGLRenderTarget renderTarget;
    private OGLTexture2D.Viewer viewer;
    private int locType;
    private float teleso = 0.0f;
    private boolean wiredView, pause = false;
    private OGLTexture2D textureMosaic;
    private int slunceX=0;
    private int slunceY=0;
    private int slunceZ=0;



    @Override
    public void init() {
        glClearColor(0.1f, 0.1f, 0.1f, 1);
        glEnable(GL_DEPTH_TEST);


        shaderProgramViewer = ShaderUtils.loadProgram("/lvl1basic/p01start/start");

        shaderProgramLight = ShaderUtils.loadProgram("/lvl1basic/p01start/light");

        locView = glGetUniformLocation(shaderProgramViewer, "view");
        locProjection = glGetUniformLocation(shaderProgramViewer, "projection");
        locTime = glGetUniformLocation(shaderProgramViewer, "time");
        locType =  glGetUniformLocation(shaderProgramViewer, "type");
        locLightVP =  glGetUniformLocation(shaderProgramViewer, "lightViewProjection");
        locTeleso = glGetUniformLocation(shaderProgramViewer, "teleso");

        locViewLight = glGetUniformLocation(shaderProgramLight, "view");
        locProjectionLight = glGetUniformLocation(shaderProgramLight, "projection");
        locTimeLight = glGetUniformLocation(shaderProgramLight, "time");
        locTypeLight = glGetUniformLocation(shaderProgramLight, "type");
        locPosX = glGetUniformLocation(shaderProgramViewer,"posX");
        locPosY = glGetUniformLocation(shaderProgramViewer,"posY");
        locPosZ = glGetUniformLocation(shaderProgramViewer,"posZ");

        locTelesoLight = glGetUniformLocation(shaderProgramLight, "teleso");

        //práce s ogl soubory - textury ze souboru
        //new OGLModelOBJ(aa=new OGLModelOBJ("cesta k modelu").getBuffers());
        //aa.draw(GL_TRIANGLES, ...);



        buffers = GridFactory.generateGridTriangleStrip(100, 100);
        //glFrontFace(GL_CCW); jak budou trojúhelníčky

        renderTarget = new OGLRenderTarget(1024,1024);
        viewer = new OGLTexture2D.Viewer();
        System.out.println("loading texture");

        try {
            textureMosaic = new OGLTexture2D("./textures/mosaic.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        camLight = new Camera().withPosition(new Vec3D(slunceX, slunceY, slunceZ)).withAzimuth(5/4f*Math.PI).withZenith(-1/5f*Math.PI).withFirstPerson(false).withRadius(6);
        cam = new Camera().withPosition(new Vec3D(0, 0, 0)).withAzimuth(5/4f*Math.PI).withZenith(-1/5f*Math.PI).withFirstPerson(false).withRadius(6);

        projection = new Mat4PerspRH(Math.PI / 3,
                LwjglWindow.HEIGHT/(float) LwjglWindow.WIDTH, 1, 20);


    }
    private GLFWKeyCallback   keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            if (action == GLFW_PRESS || action == GLFW_REPEAT){
                switch (key) {
                    case GLFW_KEY_W:
                        cam = cam.forward(1);
                        break;
                    case GLFW_KEY_D:
                        cam = cam.right(1);
                        break;
                    case GLFW_KEY_S:
                        cam = cam.backward(1);
                        break;
                    case GLFW_KEY_A:
                        cam = cam.left(1);
                        break;
                    case GLFW_KEY_LEFT_CONTROL:
                        cam = cam.down(1);
                        break;
                    case GLFW_KEY_LEFT_SHIFT:
                        cam = cam.up(1);
                        break;
                    case GLFW_KEY_SPACE:
                        cam = cam.withFirstPerson(!cam.getFirstPerson());
                        break;
                    case GLFW_KEY_R:
                        cam = cam.mulRadius(0.9f);
                        break;
                    case GLFW_KEY_F:
                        if(wiredView){
                            wiredView = false;
                        }else{
                           wiredView= true;
                        }
                        break;
                    case GLFW_KEY_L:
                        camLight = camLight.addAzimuth(0.1);
                        break;
                    case GLFW_KEY_K:
                        camLight = camLight.addAzimuth(-0.1);
                        break;
                    case GLFW_KEY_T:
                        if(teleso==0){
                            teleso = 1;
                        }else{
                            teleso=0;
                        }
                        break;
                     case GLFW_KEY_P:
                         if(pause==false){
                             pause = true;
                         }else{
                             pause=false;
                         }
                        break;
                }
            }
        }
    };

    private GLFWWindowSizeCallback wsCallback = new GLFWWindowSizeCallback() {
        @Override
        public void invoke(long window, int w, int h) {
            if (w > 0 && h > 0 &&
                    (w != width || h != height)) {
                width = w;
                height = h;
                projection = new Mat4PerspRH(Math.PI / 4, height / (double) width, 0.01, 1000.0);
                if (textRenderer != null)
                    textRenderer.resize(width, height);
            }
        }
    };

    private GLFWMouseButtonCallback mbCallback = new GLFWMouseButtonCallback () {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            mouseButton1 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;

            if (button==GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS){
                mouseButton1 = true;
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                ox = xBuffer.get(0);
                oy = yBuffer.get(0);
            }

            if (button==GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE){
                mouseButton1 = false;
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                double x = xBuffer.get(0);
                double y = yBuffer.get(0);
                cam = cam.addAzimuth((double) Math.PI * (ox - x) / width)
                        .addZenith((double) Math.PI * (oy - y) / width);
                ox = x;
                oy = y;
            }
        }
    };

    private GLFWCursorPosCallback cpCallbacknew = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double x, double y) {
            if (mouseButton1) {
                cam = cam.addAzimuth((double) Math.PI * (ox - x) / width)
                        .addZenith((double) Math.PI * (oy - y) / width);
                ox = x;
                oy = y;
            }
        }
    };

    private GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override
        public void invoke(long window, double dx, double dy) {
            if (dy < 0)
                cam = cam.mulRadius(0.9f);
            else
                cam = cam.mulRadius(1.1f);

        }
    };

    @Override
    public GLFWKeyCallback getKeyCallback() {
        return keyCallback;
    }

    @Override
    public GLFWWindowSizeCallback getWsCallback() {
        return wsCallback;
    }

    @Override
    public GLFWMouseButtonCallback getMouseCallback() {
        return mbCallback;
    }

    @Override
    public GLFWCursorPosCallback getCursorCallback() {
        return cpCallbacknew;
    }

    @Override
    public GLFWScrollCallback getScrollCallback() {
        return scrollCallback;
    }

    @Override
    public void display() {

        renderFromLight();

        renderFromViewer();

        glViewport(0,0,width,height);
        viewer.view(renderTarget.getColorTexture(),-1,0,0.5);
        viewer.view(renderTarget.getDepthTexture(),-1,-0.5,0.5);
        //viewer.view(renderTarget.getColorTexture(),-0.75,-0.75,2);
    }

    private void renderFromLight() {
        glUseProgram(shaderProgramLight);
        renderTarget.bind();

        glUniform1f(locTelesoLight, teleso);
        glClearColor(0,0.5f,0, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glUniformMatrix4fv(locViewLight, false, camLight.getViewMatrix().floatArray());
        glUniformMatrix4fv(locProjectionLight, false, projection.floatArray());

        glUniform1f(locTimeLight, time);

        glUniform1f(locTypeLight,0);
        buffers.draw(GL_TRIANGLE_STRIP, shaderProgramLight);

        glUniform1f(locTypeLight, 1);


        buffers.draw(GL_TRIANGLE_STRIP, shaderProgramLight);

        glUniform1f(locTypeLight, 2);

        buffers.draw(GL_TRIANGLE_STRIP, shaderProgramLight);

    }

    private void renderFromViewer() {
        glUseProgram(shaderProgramViewer);
        glViewport(0, 0, width, height);

        glUniform1f(locTeleso, teleso);
        //defaultn9 framebuffer - render do obrazovky
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        glClearColor(0.5f,0,0, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);  //vyčištění barvy a zbufferu
        glUniformMatrix4fv(locView, false, cam.getViewMatrix().floatArray());
        glUniformMatrix4fv(locProjection, false, projection.floatArray());
        textureMosaic.bind(shaderProgramViewer, "textureMosaic",0);
        glUniformMatrix4fv(locLightVP, false, camLight.getViewMatrix().mul(projection).floatArray());
        renderTarget.getDepthTexture().bind(shaderProgramViewer, "depthTexture", 1);
        time += 0.1;
        glUniform1f(locTime, time);
        glUniform1f(locType, 0);
        if(wiredView) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        }else{
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }
        buffers.draw(GL_TRIANGLE_STRIP, shaderProgramViewer);

        glUniform1f(locType, 1);

      //  buffers.draw(GL_TRIANGLES, shaderProgramViewer);   duplicita?

        buffers.draw(GL_TRIANGLE_STRIP, shaderProgramViewer);
        glUniform1f(locType, 2);
        if(pause == false)
        camLight = camLight.addAzimuth(0.01);
        glUniform1f(locPosX, (float) camLight.getEye().getX());
        glUniform1f(locPosY, (float) camLight.getEye().getY());
        glUniform1f(locPosZ, (float) camLight.getEye().getZ());


        buffers.draw(GL_TRIANGLE_STRIP, shaderProgramViewer);





    }
}