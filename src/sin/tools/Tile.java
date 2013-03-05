package sin.tools;

import com.jme3.scene.VertexBuffer;
import com.jme3.material.Material;
import com.jme3.asset.AssetManager;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import com.jme3.scene.Node;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.Vector3f;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import java.util.EnumMap;

/**
 *
 * @author Logan
 */
public class Tile {
    private boolean areWeWrapping = false;
    public int wrapHeight = 1;
    //public int height;
    public Type type;
    private Geometry myCore;
    public FloatBuffer VertexAlpha;
    public static final int NO_WRAP = 0;
    public static EnumMap<Type, Material> Materials = new EnumMap<Type, Material>(Type.class);
    //public static final float HEIGHTSCALE = 0.25f;
    //private static final float INVHEIGHTSCALE = 1 / HEIGHTSCALE;
    private static boolean initialized = false;
    private static FloatBuffer VERTEX_DATA;
    private static FloatBuffer NORMALS_DATA;
    private static FloatBuffer LID_TEXTURE_DATA;
    private static ShortBuffer LID_INDICES_DATA;
    private static ShortBuffer UNDER_INDICES_DATA;

    public enum Type {

        Air,
        Grass(T.getMaterialPath("BC_Tex"), 0.25f);
        public final String texturePath;
        public final float wrapDimensions;

        Type() {
            texturePath = null;
            wrapDimensions = 1;
        }

        Type(String tp, float wd) {
            texturePath = tp;
            wrapDimensions = wd;
        }
    }

    public static void initialize(AssetManager assetManager) {
        if (!initialized) {
            for (Type typ : Type.values()) {
                if (typ.texturePath != null) {
                    Material tempMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    Texture tempTex = assetManager.loadTexture(typ.texturePath);
                    tempTex.setWrap(Texture.WrapAxis.S, Texture.WrapMode.Repeat);
                    tempMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
                    tempMat.setTexture("ColorMap", tempTex);
                    Materials.put(typ, tempMat);
                }
            }
            initBuffers();
            initialized = true;
        }
    }

    private static void initBuffers() {
        final float[] VD = {
            0, 0, 0,
            1, 0, 0,
            1, 1, 0,
            0, 1, 0,
            1, 0, 1,
            1, 1, 1,
            0, 1, 1,
            0, 0, 1,
            0, 1, 1,
            0, 0, 1,};
        final float[] LIDTD = {
            0.99f, 0.49f,
            0.99f, 0.01f,
            0.99f, 0.49f,
            0.99f, 0.01f,
            0.5f, 0.01f,
            0.5f, 0.49f,
            0.5f, 0.01f,
            0.5f, 0.49f,
            0.01f, 0.49f,
            0.01f, 0.01f,};

        final short[] LIDID = {
            8, 9, 4, 5, 8, 4, // TOP
            5, 4, 1, 2, 5, 1, // NORTH
            3, 0, 7, 6, 3, 7, //SOUTH
            5, 3, 6, 5, 2, 3, // WEST
            1, 4, 7, 1, 7, 0, // EAST
        };
        final short[] UNDID = {
            2, 1, 0, 3, 2, 0, // BOTTOM
            5, 4, 1, 2, 5, 1, // NORTH
            3, 0, 7, 6, 3, 7, //SOUTH
            5, 3, 6, 5, 2, 3, // WEST
            1, 4, 7, 1, 7, 0, // EAST
        };
        final float[] ND = {
            -1, -1, -1,
            1, -1, -1,
            1, 1, -1,
            -1, 1, -1,
            1, -1, 1,
            1, 1, 1,
            -1, 1, 1,
            -1, -1, 1,
            -1, 1, 1,
            -1, -1, 1,};
        LID_TEXTURE_DATA = BufferUtils.createFloatBuffer(LIDTD);
        VERTEX_DATA = BufferUtils.createFloatBuffer(VD);
        LID_INDICES_DATA = BufferUtils.createShortBuffer(LIDID);
        UNDER_INDICES_DATA = BufferUtils.createShortBuffer(UNDID);
        NORMALS_DATA = BufferUtils.createFloatBuffer(ND);

    }

    public static void SetFBAlpha(FloatBuffer which, float alpha) {
        for (int n = 3; n <= which.limit(); n += 4) {
            which.put(n, alpha);
        }
    }

    public final Geometry GetGeometry() {
        return myCore;
    }

    public final void setMaterial(Material m) {
        myCore.setMaterial(m);
    }

    public void AttachTo(Node theNode) {
        if (theNode != null) {
            theNode.attachChild(myCore);
        } else {
            myCore.removeFromParent();
        }
    }

    public final void setLocalTranslation(float x, float y, float z) {
        myCore.setLocalTranslation(x, y, z);
    }
    public final void setLocalTranslation(Vector3f trans){
        myCore.setLocalTranslation(trans);
    }

    public final void setLocalScale(float x, float y, float z) {
        myCore.setLocalScale(x, y, z);
    }
    public final void setLocalScale(Vector3f size){
        myCore.setLocalScale(size);
    }

    public final void removeFromParent() {
        myCore.removeFromParent();
    }

    public Tile(Vector3f size, Vector3f trans, Type type, int wrapHeight, Node toAttach) {
        super();
        this.wrapHeight = wrapHeight;
        this.type = type;
        myCore = new Geometry("Tile");
        myCore.setMesh(new Mesh());
        if (wrapHeight > 0) {
            this.areWeWrapping = true;
        }
        updateGeometry();
        setMaterial(Materials.get(type));
        toAttach.attachChild(this.myCore);
        setLocalTranslation(trans);
        setLocalScale(size);
    }

    public void ChangeType(Type type) {
        setMaterial(Materials.get(type));
    }

    public void destroy() {
        removeFromParent();
        myCore = null;
        myCore.setMesh(null);
    }

    public boolean IsLid() {
        return !areWeWrapping;
    }

    /**
     * Empty constructor for serialization only. Do not use.
     */
    public Tile() {
        super();
    }

    public float GetAlpha() {
        return VertexAlpha.get(3);
    }

    public void setAlpha(float alpha) {
        if (alpha < 0) {
            alpha = 0;
        }
        if (alpha > 1) {
            alpha = 1;
        }
        if (GetAlpha() != alpha) {
            SetFBAlpha(VertexAlpha, alpha);
            if (alpha == 1) {
                myCore.setQueueBucket(Bucket.Inherit);
                myCore.getMesh().setStatic();
            } else if (alpha == 0) {
                myCore.setQueueBucket(Bucket.Transparent);
                myCore.getMesh().setDynamic();
            } else if (alpha > 0 && alpha < 1 && myCore.getQueueBucket() != Bucket.Transparent) {
                myCore.setQueueBucket(Bucket.Transparent);
                myCore.getMesh().setDynamic();
            }
        }
    }

    public final void updateGeometry() {
        duUpdateGeometryVertices();
        duUpdateGeometryNormals();
        duUpdateGeometryTextures();
        duUpdateGeometryIndices();
        duUpdateGeometryColors();
    }

    protected void duUpdateGeometryIndices() {
        Mesh mesh = myCore.getMesh();
        if (mesh.getBuffer(VertexBuffer.Type.Index) == null) {
            if (!this.areWeWrapping) {
                mesh.setBuffer(VertexBuffer.Type.Index, 3, LID_INDICES_DATA);
            } else {
                mesh.setBuffer(VertexBuffer.Type.Index, 3, UNDER_INDICES_DATA);
            }
        }
    }

    protected void duUpdateGeometryNormals() {
        Mesh mesh = myCore.getMesh();
        if (mesh.getBuffer(VertexBuffer.Type.Normal) == null) {
            mesh.setBuffer(VertexBuffer.Type.Normal, 3, NORMALS_DATA);
        }
    }

    protected void duUpdateGeometryTextures() {
        Mesh mesh = myCore.getMesh();

        if (mesh.getBuffer(VertexBuffer.Type.TexCoord) == null) {
            if (!this.areWeWrapping) {
                mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, LID_TEXTURE_DATA);
            } else {
                float w = this.wrapHeight * type.wrapDimensions;
                w *= 0.5f;
                final float[] UNDER_TEXTURE_DATA = {
                    0, 0.51f,
                    0, 0.99f,
                    0, 0.51f,
                    0, 0.99f,
                    w, 0.99f,
                    w, 0.51f,
                    w, 0.99f,
                    w, 0.51f,
                    0, 0,
                    0, 0,};
                mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(UNDER_TEXTURE_DATA));

            }
        }
    }

    protected void duUpdateGeometryColors() {
        Mesh mesh = myCore.getMesh();

        if (mesh.getBuffer(VertexBuffer.Type.Color) == null) {
            final float[] LID_COLOR_DATA = {
                1, 1, 1, 0,
                1, 1, 1, 0,
                1, 1, 1, 0,
                1, 1, 1, 0,
                1, 1, 1, 0,
                1, 1, 1, 0,
                1, 1, 1, 0,
                1, 1, 1, 0,
                1, 1, 1, 0,
                1, 1, 1, 0,};
            VertexAlpha = BufferUtils.createFloatBuffer(LID_COLOR_DATA);
            mesh.setBuffer(VertexBuffer.Type.Color, 4, VertexAlpha);
            setAlpha(1);
        }
    }

    protected void duUpdateGeometryVertices() {
        Mesh mesh = myCore.getMesh();
        mesh.setBuffer(VertexBuffer.Type.Position, 3, VERTEX_DATA);
        mesh.updateBound();
    }
}
