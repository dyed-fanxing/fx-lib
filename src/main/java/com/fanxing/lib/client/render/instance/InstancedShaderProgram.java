package com.fanxing.lib.client.render.instance;

import com.fanxing.lib.client.render.instance.format.InstanceFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.Optional;

public class InstancedShaderProgram {
    private final int programId;
    public final InstanceFormat format;   // 统一名称
    private boolean closed;

    public InstancedShaderProgram(String vertPath, String fragPath, InstanceFormat format) {
        this.format = format;
        int vert = compileShader(GL20.GL_VERTEX_SHADER, readResource(vertPath));
        int frag = compileShader(GL20.GL_FRAGMENT_SHADER, readResource(fragPath));
        programId = GL20.glCreateProgram();
        GL20.glAttachShader(programId, vert);
        GL20.glAttachShader(programId, frag);
        GL20.glLinkProgram(programId);
        checkLink(programId);
        GL20.glDeleteShader(vert);
        GL20.glDeleteShader(frag);
    }

    private static String readResource(String path) {
        ResourceLocation loc = ResourceLocation.parse(path);
        try {
            Optional<Resource> res = Minecraft.getInstance().getResourceManager().getResource(loc);
            if (res.isEmpty()) throw new RuntimeException("Shader not found: " + loc);
            try (InputStream in = res.get().open()) {
                return new String(in.readAllBytes());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void use() { GL20.glUseProgram(programId); }
    public void unuse() { GL20.glUseProgram(0); }

    public int getUniformLocation(String name) { return GL20.glGetUniformLocation(programId, name); }
    public void setUniform1i(int loc, int v) { GL20.glUniform1i(loc, v); }
    public void setUniform1f(int loc, float v) { GL20.glUniform1f(loc, v); }
    public void setUniform4f(int loc, float a, float b, float c, float d) { GL20.glUniform4f(loc, a, b, c, d); }
    public void setUniformMatrix4f(int loc, org.joml.Matrix4f mat) {
        FloatBuffer buf = MemoryUtil.memAllocFloat(16);
        mat.get(buf);
        GL20.glUniformMatrix4fv(loc, false, buf);
        MemoryUtil.memFree(buf);
    }

    public void close() {
        if (!closed) {
            GL20.glDeleteProgram(programId);
            closed = true;
        }
    }

    private static int compileShader(int type, String source) {
        int shader = GL20.glCreateShader(type);
        GL20.glShaderSource(shader, source);
        GL20.glCompileShader(shader);
        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            String log = GL20.glGetShaderInfoLog(shader);
            GL20.glDeleteShader(shader);
            throw new RuntimeException("Shader compile error:\n" + log);
        }
        return shader;
    }

    private static void checkLink(int program) {
        if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            String log = GL20.glGetProgramInfoLog(program);
            throw new RuntimeException("Shader link error:\n" + log);
        }
    }
    public static InstancedShaderProgram create(String modId, String baseName, InstanceFormat format) {
        return new InstancedShaderProgram(
                modId + ":shaders/" + baseName + ".vsh",
                modId + ":shaders/" + baseName + ".fsh",
                format
        );
    }
}