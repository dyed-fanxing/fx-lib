#version 430 core
#moj_import <fog.glsl>

layout (std430, binding = 0) readonly buffer ParticleBuffer {
    float particles[];
};

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform int FogShape;

out float vertexDistance;
out vec2 texCoord0;
out vec4 vertexColor;

void main() {
    int id = gl_InstanceID;
    int base = id * 20;

    vec3 pos = vec3(particles[base + 0], particles[base + 1], particles[base + 2]);
    vec4 quat = vec4(particles[base + 4], particles[base + 5], particles[base + 6], particles[base + 7]);
    vec3 scale = vec3(particles[base + 8], particles[base + 9], particles[base + 10]);
    vec4 col = vec4(particles[base + 12], particles[base + 13], particles[base + 14], particles[base + 15]);
    vec4 uvRect = vec4(particles[base + 16], particles[base + 17], particles[base + 18], particles[base + 19]);

    vec2 offset;
    switch (gl_VertexID) {
        case 0: offset = vec2(-1.0, -1.0); break;
        case 1: offset = vec2(1.0, -1.0); break;
        case 2: offset = vec2(1.0, 1.0); break;
        case 3: offset = vec2(-1.0, 1.0); break;
    }
    vec3 localPos = vec3(offset.x * scale.x, offset.y * scale.y, 0.0);

    // 四元数旋转局部顶点
    vec3 t = 2.0 * cross(quat.xyz, localPos);
    vec3 rotated = localPos + quat.w * t + cross(quat.xyz, t);

    vec3 worldPos = pos + rotated;
    vec4 viewPos = ModelViewMat * vec4(worldPos, 1.0);
    gl_Position = ProjMat * viewPos;

    // 使用原版雾效函数
    vertexDistance = fog_distance(viewPos.xyz, FogShape);

    switch (gl_VertexID) {
        case 0: texCoord0 = vec2(uvRect.x, uvRect.w); break;
        case 1: texCoord0 = vec2(uvRect.z, uvRect.w); break;
        case 2: texCoord0 = vec2(uvRect.z, uvRect.y); break;
        case 3: texCoord0 = vec2(uvRect.x, uvRect.y); break;
    }
    vertexColor = col;
}