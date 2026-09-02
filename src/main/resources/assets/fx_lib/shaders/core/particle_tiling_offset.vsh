#version 150

#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;               // 整数部分表示纹理UV大小（宽高），小数部分表示纹理坐标
in ivec2 UV1;               // 由于粒子不需要覆盖层，所以用来打包表示，平铺次数+偏移（滚动速度），高位100表示平铺次数，剩余低位表示速度整数
in ivec2 UV2;              // 光照坐标

uniform sampler2D Sampler2; // 光照贴图

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform int FogShape;

out float vertexDistance;
out vec2 texCoord0;         // 局部UV
out vec4 vertexColor;
out vec2 vTilingSpeed;      // 传递给片段着色器的平铺和速度

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    vertexDistance = fog_distance(Position, FogShape);
    texCoord0 = UV0;
    vertexColor = Color * texelFetch(Sampler2, UV2 / 16, 0);
    vTilingSpeed = UV1;
}