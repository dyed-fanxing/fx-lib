#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;   // 主体灰度纹理（黑白图）
uniform sampler2D Sampler3;   // 灰度遮罩纹理（1×N，RGBA）
uniform sampler2D Sampler4;   // 着色渐变纹理（1×N，RGBA）

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;


in float vertexDistance;
in vec2 originUV;
in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    // 主体灰度纹理
    vec4 texColor = texture(Sampler0, texCoord0);
    // 遮罩纹理
    vec4 maskColor = texture(Sampler4, originUV);

    // 主纹理减去遮罩，并钳位到 [0,1] 范围
    float mask = max(0.0, texColor.r - maskColor.r);

    // 获取渐变纹理尺寸（假设高度为1，宽度任意）
    ivec2 texSize = textureSize(Sampler3, 0);
    int width = texSize.x;
    // 将 mask 映射到纹素坐标范围 [0, width-1]
    float pos = mask * float(width - 1);
    int idx0 = int(floor(pos));
    int idx1 = min(idx0 + 1, width - 1);
    float frac = fract(pos);

    // 手动采样两个相邻纹素
    vec4 color0 = texelFetch(Sampler3, ivec2(idx0, 0), 0);
    vec4 color1 = texelFetch(Sampler3, ivec2(idx1, 0), 0);




    // 线性插值获得最终颜色（包含 Alpha 通道）
    vec4 grad = mix(color0, color1, frac);

    vec4 finalColor = vec4(grad.rgb, mask) * vertexColor * ColorModulator;
    // 应用雾效
    fragColor = linear_fog(finalColor, vertexDistance, FogStart, FogEnd, FogColor);
}