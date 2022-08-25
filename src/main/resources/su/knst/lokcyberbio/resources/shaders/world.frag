#version 140

uniform sampler2D texture;
in vec4 out_color;

uniform vec4 color;
uniform bool useTexture;

void main() {
    gl_FragColor = out_color;
}