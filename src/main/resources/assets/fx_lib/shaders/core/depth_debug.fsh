#version 150

in vec2 texCoord0;
out vec4 fragColor;

void main() {
    float depth = gl_FragCoord.z;
    fragColor = vec4(depth, depth, depth, 1.0);
}