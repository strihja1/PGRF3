package lvl2advanced.p01gui.p01simple;


import lwjglutils.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL30;
import transforms.*;

import java.io.IOException;
import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL20C.*;


/**
 * @author PGRF FIM UHK
 * @version 2.0
 * @since 2019-09-02
 */
public class Renderer extends AbstractRenderer {
    private OGLBuffers buffers;
    private int locView, locPosX, locPosZ, locReflector, locSpotCutOff,locViewLightVertex, locPosY, locProjection, locTime, locViewLight, shaderProgramLight, shaderProgramViewer, locProjectionLight, locTimeLight, locTypeLight, locLightVP, locTeleso, locTelesoLight, locMode;
    private double ox, oy;
    private boolean mouseButton1 = false;
    private Camera cam, camLight;
    private Mat4 projection;
    private float time;
    private OGLRenderTarget renderTarget;
    private OGLTexture2D.Viewer viewer;
    private int locType;
    private float teleso = 0.0f;
    private float mode = 0.0f;
    private float spotCutOff = 0.6f;
    private boolean wiredView, reflector, pause = false;
    private OGLTexture2D textureMosaic;
    private boolean perspectiveProjection = true;
    private boolean malyGrid = false;




    @Override
    public void init() {
        glClearColor(0.1f, 0.1f, 0.1f, 1);
        glEnable(GL_DEPTH_TEST);


        shaderProgramViewer = ShaderUtils.loadProgram("/lvl1basic/p01start/start");

        shaderProgramLight = ShaderUtils.loadProgram("/lvl1basic/p01start/light");

        locView = glGetUniformLocation(shaderProgramViewer, "view");
        locViewLightVertex = glGetUniformLocation(shaderProgramViewer, "viewLight");
        locProjection = glGetUniformLocation(shaderProgramViewer, "projection");
        locTime = glGetUniformLocation(shaderProgramViewer, "time");
        locType = glGetUniformLocation(shaderProgramViewer, "type");
        locLightVP = glGetUniformLocation(shaderProgramViewer, "lightViewProjection");
        locTeleso = glGetUniformLocation(shaderProgramViewer, "teleso");
        locMode = glGetUniformLocation(shaderProgramViewer, "mode");
        locSpotCutOff = glGetUniformLocation(shaderProgramViewer, "spotCutOff");
        locReflector = glGetUniformLocation(shaderProgramViewer, "reflector");

        locViewLight = glGetUniformLocation(shaderProgramLight, "view");
        locProjectionLight = glGetUniformLocation(shaderProgramLight, "projection");
        locTimeLight = glGetUniformLocation(shaderProgramLight, "time");
        locTypeLight = glGetUniformLocation(shaderProgramLight, "type");
        locPosX = glGetUniformLocation(shaderProgramViewer, "posX");
        locPosY = glGetUniformLocation(shaderProgramViewer, "posY");
        locPosZ = glGetUniformLocation(shaderProgramViewer, "posZ");

        locTelesoLight = glGetUniformLocation(shaderProgramLight, "teleso");

        //práce s ogl soubory - textury ze souboru
        //new OGLModelOBJ(aa=new OGLModelOBJ("cesta k modelu").getBuffers());
        //aa.draw(GL_TRIANGLES, ...);

        buffers = GridFactory.generateGridTriangleStrip(100, 100);
        //glFrontFace(GL_CCW); jak budou trojúhelníčky

        renderTarget = new OGLRenderTarget(1024, 1024);
        viewer = new OGLTexture2D.Viewer();
        System.out.println("loading texture");

        try {
            textureMosaic = new OGLTexture2D("./textures/mosaic.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        int slunceX = 0, slunceY = 0, slunceZ = 0;
        camLight = new Camera().withPosition(new Vec3D(slunceX, slunceY, slunceZ)).withAzimuth(5 / 4f * Math.PI).withZenith(-1 / 5f * Math.PI).withFirstPerson(false).withRadius(6);
        cam = new Camera().withPosition(new Vec3D(0, 0, 0)).withAzimuth(5 / 4f * Math.PI).withZenith(-1 / 5f * Math.PI).withFirstPerson(false).withRadius(6);
        setPerspectiveProjection();


        textRenderer = new OGLTextRenderer(width, height);
    }


    @Override
    public void display() {

        renderFromLight();

        renderFromViewer();


        glViewport(0, 0, width, height);
        viewer.view(renderTarget.getColorTexture(), -1, 0, 0.5);
        viewer.view(renderTarget.getDepthTexture(), -1, -0.5, 0.5);

        textRenderer.clear();
        textRenderer.addStr2D(3, 20, "ahoj");
        textRenderer.addStr2D(width-90, height-3, " (c) PGRF UHK");
        textRenderer.draw();
        glEnable(GL_DEPTH_TEST);
    }

    private void renderFromLight() {
        glUseProgram(shaderProgramLight);
        renderTarget.bind();

        glUniform1f(locTelesoLight, teleso);
        glClearColor(0, 0.5f, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glUniformMatrix4fv(locViewLight, false, camLight.getViewMatrix().floatArray());
        glUniformMatrix4fv(locProjectionLight, false, projection.floatArray());

        glUniform1f(locTimeLight, time);

        glUniform1f(locTypeLight, 0);
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
        glClearColor(0.5f, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);  //vyčištění barvy a zbufferu
        glUniformMatrix4fv(locView, false, cam.getViewMatrix().floatArray());
        glUniformMatrix4fv(locViewLightVertex, false, camLight.getViewMatrix().floatArray());
        glUniformMatrix4fv(locProjection, false, projection.floatArray());
        textureMosaic.bind(shaderProgramViewer, "textureMosaic", 0);
        glUniformMatrix4fv(locLightVP, false, camLight.getViewMatrix().mul(projection).floatArray());
        renderTarget.getDepthTexture().bind(shaderProgramViewer, "depthTexture", 1);
        time += 0.1;
        glUniform1f(locSpotCutOff, spotCutOff);
        if(reflector) {
            glUniform1f(locReflector, 1.0f);
        }else{
            glUniform1f(locReflector, 0.0f);
        }
        glUniform1f(locTime, time);
        glUniform1f(locType, 0);
        if (wiredView) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        } else {
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }
        glUniform1f(locMode, mode);

        buffers.draw(GL_TRIANGLE_STRIP, shaderProgramViewer);

        glUniform1f(locType, 1);

        buffers.draw(GL_TRIANGLE_STRIP, shaderProgramViewer);
        glUniform1f(locType, 2);
        if (!pause)
            camLight = camLight.addAzimuth(-0.01);
        glUniform1f(locPosX, (float) camLight.getEye().getX());
        glUniform1f(locPosY, (float) camLight.getEye().getY());
        glUniform1f(locPosZ, (float) camLight.getEye().getZ());


        buffers.draw(GL_TRIANGLE_STRIP, shaderProgramViewer);


    }
    private GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            if (action == GLFW_PRESS || action == GLFW_REPEAT) {
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
                        wiredView = !wiredView;
                        break;
                    case GLFW_KEY_L:
                        camLight = camLight.addAzimuth(0.1);
                        break;
                    case GLFW_KEY_K:
                        camLight = camLight.addAzimuth(-0.1);
                        break;
                    case GLFW_KEY_H:
                        spotCutOff = spotCutOff-0.05f;
                        break;
                    case GLFW_KEY_J:
                        spotCutOff = spotCutOff+0.05f;
                        break;
                    case GLFW_KEY_B:
                      changeProjection();
                        break;
                    case GLFW_KEY_T:
                        if (teleso < 5) {
                            teleso++;
                        } else {
                            teleso = 0;
                        }
                        break;
                    case GLFW_KEY_M:
                        if (mode < 6) {
                            mode++;
                        } else {
                            mode = 0;
                        }
                        break;
                    case GLFW_KEY_P:
                        pause = !pause;
                        break;
                    case GLFW_KEY_C:
                        reflector = !reflector;
                        break;
                    case GLFW_KEY_X:
                        malyGrid = !malyGrid;
                        changeGridSize();
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

                projection = new Mat4PerspRH(Math.PI / 4, height / (double) width, 1, 200);
                if (textRenderer != null)
                    textRenderer.resize(width, height);
            }
        }
    };

    private GLFWMouseButtonCallback mbCallback = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            mouseButton1 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;

            if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS) {
                mouseButton1 = true;
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                ox = xBuffer.get(0);
                oy = yBuffer.get(0);
            }

            if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE) {
                mouseButton1 = false;
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                double x = xBuffer.get(0);
                double y = yBuffer.get(0);
                cam = cam.addAzimuth( Math.PI * (ox - x) / width)
                        .addZenith( Math.PI * (oy - y) / width);
                ox = x;
                oy = y;
            }
        }
    };

    private GLFWCursorPosCallback cpCallbacknew = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double x, double y) {
            if (mouseButton1) {
                cam = cam.addAzimuth( Math.PI * (ox - x) / width)
                        .addZenith( Math.PI * (oy - y) / width);
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

    private void changeProjection(){
        if(perspectiveProjection){
            setOrtho();
        }else{
            setPerspectiveProjection();
        }
    }

    private void setPerspectiveProjection(){
            projection = new Mat4PerspRH(Math.PI / 3,LwjglWindow.HEIGHT / (float) LwjglWindow.WIDTH, 1, 200);
            perspectiveProjection = true;
    }
    private void setOrtho(){
        projection = new Mat4OrthoRH(-30, -30, 0.01, 200);
        perspectiveProjection = false;
    }

    private void changeGridSize(){
        if(malyGrid)
            buffers = GridFactory.generateGridTriangleStrip(10, 10);
        else
            buffers = GridFactory.generateGridTriangleStrip(100, 100);
    }

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

}

// TODO Vytvořte vhodné pixelové programy pro zobrazení povrchu těles znázorňující barevně pozici (souřadnici xyz, hloubku), barvu, texturu, normálu a souřadnice do textury.
// TODO Reflektorový zdroj světla a útlum prostředí. Implementujte hladký přechod na okraji reflektoro  vého světla.
// TODO Na vhodných tělesech znázorněte rozdíl mezi výpočtem osvětlení per vertex a per pixel.
// TODO ??? Implementujte metodu pro výpočet vržených stínů ShadowMaps. Uvažujte alespoň jeden pohybující se zdroj světla a dvě různá zároveň zobrazená tělesa. Alespoň jedno těleso se musí pohybovat. Pro znázornění vržených stínu vykreslete rovinnou podložku s vypočteným osvětlením. ????