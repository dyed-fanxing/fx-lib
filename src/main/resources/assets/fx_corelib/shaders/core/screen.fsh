#version 150

uniform sampler2D Sampler0;

in vec2 texCoord0;
in vec4 vertexColor;   // 顶点颜色（包含 Alpha）

out vec4 fragColor;

void main() {
    vec4 baseColor = texture(Sampler0, texCoord0);
    // 限制最大强度为 0.8（可调）
    float maxIntensity = 0.8;
    float intensity = vertexColor.a * maxIntensity;
    // 屏幕混合公式
    vec3 result = 1.0 - (1.0 - baseColor.rgb) * (1.0 - intensity);
    fragColor = vec4(result, baseColor.a);
}