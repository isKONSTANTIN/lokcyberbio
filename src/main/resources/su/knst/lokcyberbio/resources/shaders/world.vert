#version 140
#extension GL_ARB_explicit_attrib_location : enable

uniform mat4 projection_matrix;
uniform mat4 view_matrix;

in vec2 vertex;
in vec2 pos;
in vec3 in_color;
in float size;

out vec3 out_color;

void main() {
    gl_Position = projection_matrix * view_matrix * vec4(vertex * size + pos, 0, 1);

    out_color = in_color;
}